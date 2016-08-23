# Monitor for CCIO ImMan Image Caching and Manipulation Clusters #

Put the JSON file generated by [ImMan Tools](https://github.com/CloudCluster/imman-tools) during ImMan CLuster Set Up process into **/opt/ccio/clusters/** folder and run the Monitor:

```
java -jar imman-monitor.jar {DigitalOcean token}
```

The monitor will pull cluster's statistic and restart the problematic node if any. You'll be notified by email about the problem. 
To configure the email notifications and logs copy **ccio-monitor.logback.xml** file into **/opt** folder, and make sure all emails set correctly there.

If you set up ImMan Cluster manually, without the [ImMan Tools](https://github.com/CloudCluster/imman-tools), you can build the JSON file yourself, make sure it has the same name as your ImMan CLuster name, and following structure:

```json
{
  "clusterName" : "imman-test",
  "imageNodes" : [ {
    "dropletId" : 15642031,
    "dropletName" : "imman-test-00",
    "privateIp" : "10.132.68.214",
    "publicIp" : "45.55.157.141",
  }, {
    "dropletId" : 15642022,
    "dropletName" : "imman-test-01",
    "privateIp" : "10.132.68.211",
    "publicIp" : "45.55.175.35",
  }, {
    "dropletId" : 15642022,
    "dropletName" : "imman-test-02",
    "privateIp" : "10.132.68.24",
    "publicIp" : "45.55.171.89",
  } ],
  "secret" : "yourSecretRandomString"
}
```

The JSON should be in **/opt/ccio/clusters/imman-test.json** file. You could have more than one ImMan Cluster JSON in that folder, all of them will be monitored.

You can run the monitor on the smallest [DigitalOcean](https://m.do.co/c/9e592545f7b6) droplet on OS of your choice, just make sure it has the latest Java installed.