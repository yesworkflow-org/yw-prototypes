

SUBDIRS =	simulate_data_collection

all:
	for dir in $(SUBDIRS); do \
		echo; \
		echo "---------- Building $$dir --------------------" ;	\
		make -C $$dir all; \
	done

clean:
	for dir in $(SUBDIRS); do \
		echo; \
		echo "---------- Cleaning $$dir --------------------" ; \
		make -C $$dir clean; \
	done
