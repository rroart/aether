all: xhtml pdf

xhtml: docbook/DOCUMENTATION.xml docbookhtml/figures/architecture.svg
	xmlto -o docbookhtml xhtml docbook/DOCUMENTATION.xml

docbookhtml/figures/%.svg: docbook/figures/%.fig
	mkdir -p docbookhtml/figures
	fig2dev $< $@

pdf: DOCUMENTATION.pdf

DOCUMENTATION.pdf: docbook/DOCUMENTATION.xml
	xmlto pdf docbook/DOCUMENTATION.xml

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
