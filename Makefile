all: xhtml pdf

xhtml: DOCUMENTATION.xml
	xmlto xhtml DOCUMENTATION.xml

pdf: DOCUMENTATION.pdf

%.fo: %
	xsltproc -xinclude -o $@ /usr/share/xml/docbook/stylesheet/docbook-xsl-ns/fo/docbook.xsl $<

%.pdf: %.fo
	fop $< -pdf $@

SUBDIRS = core/

core:
	mkdir -p conf
ifneq ($(AETHERTMPL),)
	rsync -a $$AETHERTMPL conf/aether.xml.tmpl
endif
	$(MAKE) -C conf -f ../Makefile aether$(AETHERTYPE).xml

aether$(AETHERTYPE).xml: aether.xml.tmpl
	envsubst < $< > $@

.PHONY: $(SUBDIRS)
