package ccio.imman.monitor;

import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.Unirest;
import com.myjeeva.digitalocean.DigitalOcean;
import com.myjeeva.digitalocean.exception.DigitalOceanException;
import com.myjeeva.digitalocean.exception.RequestUnsuccessfulException;
import com.myjeeva.digitalocean.impl.DigitalOceanClient;

public class ClusterMonitor {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ClusterMonitor.class);
	private static final ScheduledExecutorService MONITOR_THREAD = Executors.newSingleThreadScheduledExecutor();
	private static final ObjectMapper MAPPER = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

	public static void main(String[] args) {
		LOGGER.warn("Starting CCIO Cluster Monitor");
		
		if(args.length != 1){
			LOGGER.error("DO Key is missing. Must be provided as a parameter");
			return;
		}
		final DigitalOcean apiClient = new DigitalOceanClient(args[0]);
		
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
							String stat = Unirest.get("http://"+cluster.getImageNodes().get(new Random().nextInt(cluster.getImageNodes().size())).getPublicIp()+"/stat/"+cluster.getSecret()).asString().getBody();
							LOGGER.debug("Stat: {}", stat);
							Stat statMap = MAPPER.readValue(stat, Stat.class);
							if(statMap.getSize() != cluster.getImageNodes().size()){
								//something is wrong
//								LOGGER.warn("Something is wrong with cluster {}", cluster.getClusterName());
								stat = Unirest.get("http://"+cluster.getImageNodes().get(new Random().nextInt(cluster.getImageNodes().size())).getPublicIp()+"/stat/"+cluster.getSecret()).asString().getBody();
								Stat statMap2 = MAPPER.readValue(stat, Stat.class);
								//we check here if we didn't get stat from frozen node. Maybe we should be checking state="ACTIVE" or "FROZEN" instead 
								if(statMap.getSize() < statMap2.getSize()){
									statMap = statMap2;
								}
								
								Set<ImageNode> missingNodes = new HashSet<>();
								for(ImageNode node : cluster.getImageNodes()){
									boolean isMissing = true;
									for(StatNode statNode : statMap.getMembers()){
										String privateIp = statNode.getNode().get("address").toString();
										privateIp = privateIp.split(":")[0];
										LOGGER.debug("Checking node with ip {}", privateIp);
										if(privateIp.equals(node.getPrivateIp())){
											isMissing = false;
											break;
										}
									}
									if(isMissing){
										missingNodes.add(node);
									}
								}
								LOGGER.info("Missing Nodes: {}", missingNodes);
								if(missingNodes.size() > cluster.getImageNodes().size()/5){
									// we are going to restart more than 20% of the nodes, something is really wrong, and needs human attention
									LOGGER.error("More than 20% nodes are down");
								} else {
									for(ImageNode node : missingNodes){
										try {
											apiClient.powerCycleDroplet(node.getDropletId());
											nodesRestarted++;
											LOGGER.warn("{} {} is restarted", node.getDropletName(), node.getPublicIp());
										} catch (DigitalOceanException e) {
											if("Droplet already has a pending event.".equals(e.getMessage())){
												apiClient.powerOnDroplet(node.getDropletId());
											}
											LOGGER.error(e.getMessage(), e);
										} catch (RequestUnsuccessfulException e) {
											LOGGER.error(e.getMessage(), e);
										}
									}
									if(missingNodes.size() > 0){
										TimeUnit.MINUTES.sleep(5);
									}
								}
							} else {
								LOGGER.info("Everything looks good");
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
