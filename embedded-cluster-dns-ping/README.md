# A S2I Java playground with Infinispan cluster
This is a very simple example on how to run an Infinispan cluster over OKD using Infinispan Java libraries
## Run it
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
Now the S2I system is building, deploying and running the application. This could take some time because of the downloading of the s2i builder image.

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
