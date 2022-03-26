all:
ifneq ($(MYSPARKMLDATAEXTERN),)
ifneq ($(MYSPARKMLDATA),)
	rsync -a $$MYSPARKMLDATAEXTERN/ $$MYSPARKMLDATA
endif
else
	mkdir -p $$MYSPARKMLDATA
	touch $$MYSPARKMLDATA/empty
endif

.PHONY: all
