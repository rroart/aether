VERSION = 0.10-SNAPSHOT
SUBDIRS = core datanucleus servicemanager-openshift servicemanager-simple elastic eureka hbase hdfs hibernate local lucene mahout-mr mahout-spark opennlp solr spark-ml swift web
DATADIRS = opennlpdata sparkmldata mahoutsparkdata

$(DATADIRS):
	$(MAKE) -C $@ 

all: core $(DATADIRS)
#calibre core datanucleus djvutxt elastic eureka hbase hdfs hibernate local lucene mahout-mr mahout-spark opennlp pdftotext solr spark-ml s3 swift tika web wvtext
#servicemanager-openshift servicemanager-simple

calibre xcore datanucleus djvutxt elastic eureka hbase hdfs hibernate local lucene mahout-mr mahout-spark opennlp pdftotext solr spark-ml s3 swift tika web wvtext:
	cd $@ && mkdir -p files && ln -f ../../../distribution/target/aether-distribution-0.10-SNAPSHOT-bin/aether-distribution-0.10-SNAPSHOT/lib/aether-$@-${VERSION}.jar files/aether-$@-${VERSION}.jar && echo docker build -t="aether-$@" .

core:
ifneq ($(AETHERTMPL),)
	rsync -a $$AETHERTMPL core/aether.xml.tmpl
endif
	$(MAKE) -C $@ -f ../Makefile aether.xml
#       $(MAKE) -C $@ -f ../Makefile stockstat-$@-${VERSION}.jar

%.xml: %.xml.tmpl
	envsubst < $< > $@

.PHONY: all $(SUBDIRS) $(DATADIRS)
