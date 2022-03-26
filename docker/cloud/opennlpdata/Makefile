all:
ifneq ($(MYOPENNLPDATAEXTERN),)
ifneq ($(MYOPENNLPDATA),)
	rsync -a $$MYOPENNLPDATAEXTERN/ $$MYOPENNLPDATA
endif
else
	mkdir -p $$MYOPENNLPDATA
	touch $$MYOPENNLPDATA/empty
endif

.PHONY: all
