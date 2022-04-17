VERSION = 0.10-SNAPSHOT
SUBDIRS = calibre djvutxt pdftotext tika wvtext elastic solr lucene hbase hibernate cassandra dynamodb s3 swift hdfs opennlp mahoutspark sparkml core webr hibernatedata lucenedata mahoutsparkdata opennlpdata sparkmldata

all: $(SUBDIRS) skaffoldconvert.yaml skaffoldfrontr.yaml skaffoldsearchengine.yaml skaffoldclassify.yaml skaffolddatabase.yaml skaffoldfilesystem.yaml skaffoldcore.yaml conf

$(SUBDIRS):
	$(MAKE) -C $@ -f ../Makefile deployment.yaml
#%: %/deployment.yaml

%/deployment.yaml: %/deployment.yaml.tmpl
	envsubst < %/deployment.yaml.tmpl > %/deployment.yaml

deployment.yaml: deployment.yaml.tmpl
	envsubst < deployment.yaml.tmpl > deployment.yaml

skaffoldconvert.yaml: skaffoldconvert.yaml.tmpl
	envsubst < skaffoldconvert.yaml.tmpl > skaffoldconvert.yaml

skaffoldfrontr.yaml: skaffoldfrontr.yaml.tmpl
	envsubst < skaffoldfrontr.yaml.tmpl > skaffoldfrontr.yaml

skaffolddatabase.yaml: skaffolddatabase.yaml.tmpl
	envsubst < skaffolddatabase.yaml.tmpl > skaffolddatabase.yaml

skaffoldfilesystem.yaml: skaffoldfilesystem.yaml.tmpl
	envsubst < skaffoldfilesystem.yaml.tmpl > skaffoldfilesystem.yaml

skaffoldsearchengine.yaml: skaffoldsearchengine.yaml.tmpl
	envsubst < skaffoldsearchengine.yaml.tmpl > skaffoldsearchengine.yaml

skaffoldclassify.yaml: skaffoldclassify.yaml.tmpl
	envsubst < skaffoldclassify.yaml.tmpl > skaffoldclassify.yaml

skaffoldcore.yaml: skaffoldcore.yaml.tmpl
	envsubst < skaffoldcore.yaml.tmpl > skaffoldcore.yaml

conf:
	$(MAKE) -C ../../cloud -B

.PHONY: all $(SUBDIRS) conf
