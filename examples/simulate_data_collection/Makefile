
WORKFLOW_SCRIPT = simulate_data_collection.py

PROSPECTIVE_LINEAGE_DATA = corrected_image

#RETROSPECTIVE_LINEAGE_DATA = sample_id
#RETROSPECTIVE_LINEAGE_VALUE = 'DRT240'
RETROSPECTIVE_LINEAGE_DATA = corrected_image

SCRIPT_RUN_CMD = python simulate_data_collection.py q55 --cutoff 12 --redundancy 1 | cut -c 21-
POST_RUN_CMD = rm -f run/data/DRT240/DRT240_12000eV_002.img

NW_FILTERED_LINEAGE = true

include ../defaults.Makefile
include $(MAKEFILES_DIR)/common.Makefile
