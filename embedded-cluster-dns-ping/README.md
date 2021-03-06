# A S2I Java playground with Infinispan cluster
This is a very simple example on how to run an Infinispan cluster over OKD using Infinispan Java libraries. It's an S2I project so should be easy to play and experiment with it. Enjoy.
## Run
Open a terminal and execute the following:
```
$ oc cluster up                   # Start an OKD cluster (tested on v3.11)
$ oc project myproject            # Change project to my
# Now create the application from gituib source
$ oc new-app \
    registry.access.redhat.com/redhat-openjdk-18/openjdk18-openshift~https://github.com/rigazilla/ispn-4-okd \
    --context-dir=embedded-cluster-dns-ping
# Add an headless service to the application    
$ oc expose dc/ispn-4-okd \
    --name okddnsping-headless --cluster-ip=None
```
The S2I system will build, deploy and run the application. This could take some time because of the downloading of the s2i builder image.  
The above `oc expose` command creates an headless service associated to the application. This service is associated with a DNS entry containing all the application's pods name that will be queried by the DNS_PING protocol ([kubernetes DNS docs](https://kubernetes.io/docs/concepts/services-networking/dns-pod-service/#services)).

If everything went well, this should be the status:
```
$ oc get pods
NAME                 READY     STATUS      RESTARTS   AGE
ispn-4-okd-1-bldfv   1/1       Running     0          12m
ispn-4-okd-1-build   0/1       Completed   0          13m
```
The project contains two pods: `ispn-4-okd-1-build` is the s2i builder and `ispn-4-okd-1-bldfv' is the single component of the ISPN cluster.

## Having fun
Check the pod logs to see what's going on:
```
$ oc logs ispn-4-okd-1-bldfv | tail -5
Cluster members list: [ispn-4-okd-1-bldfv-61884]
Cluster members list: [ispn-4-okd-1-bldfv-61884]
Cluster members list: [ispn-4-okd-1-bldfv-61884]
10:20:22.705 [jgroups-17,mycluster,ispn-4-okd-1-bldfv-61884] DEBUG org.jgroups.protocols.dns.DNS_PING - ispn-4-okd-1-bldfv-61884: no entries collected from DNS (in 42 ms)
Cluster members list: [ispn-4-okd-1-bldfv-61884]
```
Everything works as design: the pod is just printing the Infinispan cluster components, see [Main.java](https://github.com/rigazilla/ispn-4-okd/blob/master/embedded-cluster-dns-ping/src/main/java/org/infinispan/tutorial/okddnsping/Main.java)
### Scaling the cluster
We can now scale the cluster to 2 pods.
Enter this command:
```
oc scale dc ispn-4-okd --replicas=2
```
check if two pods are running:
```
$ oc get pods
NAME                 READY     STATUS      RESTARTS   AGE
ispn-4-okd-1-build   0/1       Completed   0          4m
ispn-4-okd-1-k5rgw   1/1       Running     0          3m
ispn-4-okd-1-wqgrd   1/1       Running     0          6s
```
and if they are in cluster:
```
$ oc logs ispn-4-okd-1-k5rgw | tail -5
Cluster members list: [ispn-4-okd-1-k5rgw-6237, ispn-4-okd-1-wqgrd-26675]
Cluster members list: [ispn-4-okd-1-k5rgw-6237, ispn-4-okd-1-wqgrd-26675]
Cluster members list: [ispn-4-okd-1-k5rgw-6237, ispn-4-okd-1-wqgrd-26675]
Cluster members list: [ispn-4-okd-1-k5rgw-6237, ispn-4-okd-1-wqgrd-26675]
Cluster members list: [ispn-4-okd-1-k5rgw-6237, ispn-4-okd-1-wqgrd-26675]
```
# DNS_PING
JGroups DNS_PING discovery protocol is used in this example to form the cluster ([JGroups docs](http://jgroups.org/manual4/index.html#_dns_ping)), this protocol queries the DNS service (OKD provides kube-dns) to retrieve the list of the cluster members. The configuration file for Jgroups is [here](https://github.com/rigazilla/ispn-4-okd/blob/master/embedded-cluster-dns-ping/src/main/resources/cluster-dns-ping.xml) and the line related to DNS_PING is:
```
<dns.DNS_PING dns_query="okddnsping-headless.myproject.svc.cluster.local" />
```
this line sais to JGroups to discover cluster members using DNS_PING whit the given DNS query. That query matches the DNS entry associated to the headless service exposed with the application and that entry contains all the pods deployed by the application.
# Conclusion
An easy way to deploy an application with an Infinispan cluster embedded has been shown, with some more info about DNS_PING. Thanks for readme.
