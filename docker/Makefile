VERSION = 0.10-SNAPSHOT
SUBDIRS = core datanucleus servicemanager-openshift servicemanager-simple elastic eureka hbase hdfs hibernate local lucene mahout-mr mahout-spark opennlp solr spark-ml swift web

all: core datanucleus elastic eureka hbase hdfs hibernate local lucene mahout-mr mahout-spark opennlp solr spark-ml swift web
#servicemanager-openshift servicemanager-simple

core datanucleus elastic eureka hbase hdfs hibernate local lucene mahout-mr mahout-spark opennlp solr spark-ml swift web:
	cd $@ && mkdir -p files && ln -f ../../lib/aether-$@-${VERSION}.jar files/aether-$@-${VERSION}.jar && docker build -t="aether-$@" .

.PHONY: all $(SUBDIRS)
