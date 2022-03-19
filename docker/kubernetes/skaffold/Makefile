VERSION = 0.10-SNAPSHOT
SUBDIRS = calibre djvutxt pdftotext tika wvtext elastic solr

all: $(SUBDIRS) skaffoldconvert.yaml skaffoldfrontr.yaml
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

.PHONY: all $(SUBDIRS)
