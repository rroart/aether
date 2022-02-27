VERSION = 0.10-SNAPSHOT
SUBDIRS = core datanucleus servicemanager-openshift servicemanager-simple elastic eureka hbase hdfs hibernate local lucene mahout-mr mahout-spark opennlp solr spark-ml swift web

all: calibre core datanucleus djvutxt elastic eureka hbase hdfs hibernate local lucene mahout-mr mahout-spark opennlp pdftotext solr spark-ml s3 swift tika web wvtext
#servicemanager-openshift servicemanager-simple

calibre core datanucleus djvutxt elastic eureka hbase hdfs hibernate local lucene mahout-mr mahout-spark opennlp pdftotext solr spark-ml s3 swift tika web wvtext:
	cd $@ && mkdir -p files && ln -f ../../lib/aether-$@-${VERSION}.jar files/aether-$@-${VERSION}.jar && docker build -t="aether-$@" .

.PHONY: all $(SUBDIRS)
