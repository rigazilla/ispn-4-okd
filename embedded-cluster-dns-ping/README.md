# A S2I Java playground with Infinispan cluster
This is a very simple example on how to run an Infinispan cluster over OKD using Infinispan Java libraries

## Run it
Open a terminal and execute the following:
```
oc cluster up                   # Start an OKD cluster (tested on v3.11)
oc project myproject            # Change project to my
# Now create the application from gituib source
oc new-app \
    registry.access.redhat.com/redhat-openjdk-18/openjdk18-openshift~https://github.com/rigazilla/ispn-4-okd \
    --context-dir=embedded-cluster-dns-ping
```
