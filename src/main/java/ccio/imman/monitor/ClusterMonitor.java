package ccio.imman.monitor;

import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.myjeeva.digitalocean.DigitalOcean;
import com.myjeeva.digitalocean.exception.DigitalOceanException;
import com.myjeeva.digitalocean.exception.RequestUnsuccessfulException;
import com.myjeeva.digitalocean.impl.DigitalOceanClient;

public class ClusterMonitor {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ClusterMonitor.class);
	private static final ScheduledExecutorService MONITOR_THREAD = Executors.newSingleThreadScheduledExecutor();
	private static final ObjectMapper MAPPER = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	private static final HashMap<Integer, Long> LAST_RESTART = new HashMap<>();
	
	public static void main(String[] args) {
		LOGGER.warn("Starting CCIO Cluster Monitor");
		
		MONITOR_THREAD.scheduleWithFixedDelay(new Runnable() {
			
			private LocalTime time = LocalTime.now();
			private int nodesRestarted = 0;
			
			@Override
			public void run() {
				if(time.plusHours(24).isAfter(LocalTime.now())){
					LOGGER.warn("{} nodes restarted", nodesRestarted);
					time = LocalTime.now();
					nodesRestarted = 0;
				}
				try (DirectoryStream<Path> dirStream = Files.newDirectoryStream(Paths.get("/opt/ccio/clusters/"), "*.json")) {
			        dirStream.forEach(path -> {
			        	try {
			        		LOGGER.info("Checking {}", path);
							ImageCluster cluster = MAPPER.readValue(path.toFile(), ImageCluster.class);
							
							final DigitalOcean apiClient = new DigitalOceanClient(cluster.getDoToken());
							
							LOGGER.debug("======================$$$$$$$$$$$$$$===================");
							
							int ok = 0;
							for(ImageNode node : cluster.getImageNodes()){
								try{
									LOGGER.debug("+++++++++++++++++++++++++++++++++");
									LOGGER.debug("### {}: ", node.getDropletName());
									JsonNode stat = Unirest.get("http://"+node.getPublicIp()+"/stat/"+cluster.getSecret())
											.asJson().getBody();
									LOGGER.debug("Stat {}: {}", node.getDropletName(), stat);
									String status = stat.getObject().getString("status");
									if(!"ok".equals(status)){
										throw new Exception();
									}
									ok++;
								} catch (Exception ex) {
									LOGGER.debug("######### RESTART 1 #########: ", ex.getMessage());
									Long lastReastart = LAST_RESTART.get(node.getDropletId());
									if(lastReastart == null || lastReastart+(7*60*1000) < System.currentTimeMillis()){
										LOGGER.debug("######### RESTART 2 #########");
										try {
											apiClient.powerCycleDroplet(node.getDropletId());
											LAST_RESTART.put(node.getDropletId(), System.currentTimeMillis());
											nodesRestarted++;
											LOGGER.warn("{} {} is restarted", node.getDropletName(), node.getPublicIp());
										} catch (DigitalOceanException e) {
											LOGGER.debug("######### RESTART 3 #########: ", e.getMessage());
											if("Droplet already has a pending event.".equals(e.getMessage())){
												apiClient.powerOnDroplet(node.getDropletId());
											}
											LOGGER.error(e.getMessage(), e);
										} catch (RequestUnsuccessfulException e) {
											LOGGER.error(e.getMessage(), e);
										}
									}
							    }
							}
							if(ok == cluster.getImageNodes().size()){
								LOGGER.info("Everything looks ok");
							}
						} catch (Exception e) {
							LOGGER.error(e.getMessage(), e);
						}
			        });
				} catch (Throwable th) {
					LOGGER.error(th.getMessage(), th);
				}				
			}
		}, 1, 1, TimeUnit.MINUTES);
	}
}
