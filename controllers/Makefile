TARGETS=MyController MyController

.PHONY: $(TARGETS)

build clean:
	@+for X in $(TARGETS); do \
	  echo $@ing $$X; \
	  make -s -C $$X $@ ; \
	done
	
