VERSION = 0.10-SNAPSHOT
SUBDIRS = calibre djvutxt pdftotext tika wvtext

all: $(SUBDIRS)
$(SUBDIRS):
	$(MAKE) -C $@ -f ../Makefile deployment.yaml
#%: %/deployment.yaml

%/deployment.yaml: %/deployment.yaml.tmpl
	envsubst < %/deployment.yaml.tmpl > %/deployment.yaml

deployment.yaml: deployment.yaml.tmpl
	envsubst < deployment.yaml.tmpl > deployment.yaml

.PHONY: all $(SUBDIRS)
