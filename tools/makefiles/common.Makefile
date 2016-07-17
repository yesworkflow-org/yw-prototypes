
BASH_VARIABLES = SCRIPT_SETTINGS_FILE=../default_script_settings.sh
BASH = $(BASH_VARIABLES) bash

YW_EXTRACT_FACTS = $(FACTS_DIR)/yw_extract_facts.P
YW_MODEL_FACTS = $(FACTS_DIR)/yw_model_facts.P
YW_RECON_FACTS = $(FACTS_DIR)/yw_recon_facts.P
YW_FACTS = $(YW_EXTRACT_FACTS) $(YW_MODEL_FACTS) $(YW_RECON_FACTS)
YW_VIEWS = $(FACTS_DIR)/yw_views.P
YW_MODEL_OPTIONS = -c extract.language=python \
        	       -c extract.factsfile=$(YW_EXTRACT_FACTS) \
                   -c model.factsfile=$(YW_MODEL_FACTS) \
                   -c query.engine=xsb
YW_RECON_OPTIONS = -c extract.language=python \
			       -c recon.factsfile=$(YW_RECON_FACTS) \
                   -c query.engine=xsb \
				   -c recon.rundir=.

RUN_STDOUT = run_outputs.txt
RUN_OUTPUTS = $(RUN_STDOUT)

ifndef SCRIPT_RUN_CMD
SCRIPT_RUN_CMD = $(WORKFLOW_SCRIPT)
endif

RULES = $(RULES_DIR)/yw_views.P
QUERY_SCRIPT = query.sh
QUERY_OUTPUTS = query_outputs.txt

YW_DATA_GRAPH = yw_data_graph
YW_PROCESS_GRAPH = yw_process_graph
YW_COMBINED_GRAPH = yw_combined_graph
YW_PROSPECTIVE_LINEAGE_GRAPH = yw_prospective_lineage

YW_DATA_GRAPH_GV = $(GRAPHS_DIR)/$(YW_DATA_GRAPH).gv
YW_PROCESS_GRAPH_GV = $(GRAPHS_DIR)/$(YW_PROCESS_GRAPH).gv
YW_COMBINED_GRAPH_GV =$(GRAPHS_DIR)/$(YW_COMBINED_GRAPH).gv
YW_PROSPECTIVE_LINEAGE_GRAPH_GV = $(GRAPHS_DIR)/$(YW_PROSPECTIVE_LINEAGE_GRAPH).gv

YW_GRAPHS = $(YW_DATA_GRAPH_GV) \
			$(YW_PROCESS_GRAPH_GV) \
			$(YW_COMBINED_GRAPH_GV) \
			$(YW_PROSPECTIVE_LINEAGE_GRAPH_GV)

GRAPHS = $(YW_GRAPHS)
PNGS = $(GRAPHS:.gv=.png)
PDFS = $(GRAPHS:.gv=.pdf)

all: run yw query graph $(CUSTOM_TARGETS)
run: $(RUN_OUTPUTS)
yw: $(YW_FACTS) $(YW_VIEWS)
recon: $(YW_RECON_FACTS)
query: $(QUERY_OUTPUTS)
graph: $(GRAPHS)
png: $(PNGS)
pdf: $(PDFS)

.SUFFIXES:
.SUFFIXES: .gv .pdf .png

.gv.pdf:
	dot -Tpdf $*.gv -o $*.pdf

.gv.png:
	dot -Tpng $*.gv -o $*.png

ifdef DATA
PROSPECTIVE_LINEAGE_DATA = $(DATA)
.PHONY: $(YW_PROSPECTIVE_LINEAGE_GRAPH_GV)
endif



.yw_setup:
ifdef YW_JAR
	mkdir -p facts
	touch .yw_setup
else
	$(error Must set YW_JAR environment variable to path to YesWorkflow jar)
endif

YW_CMD = java -jar $(YW_JAR)

$(YW_EXTRACT_FACTS) $(YW_MODEL_FACTS): .yw_setup $(WORKFLOW_SCRIPT)
	$(YW_CMD) model $(WORKFLOW_SCRIPT) $(YW_MODEL_OPTIONS)

$(YW_RECON_FACTS): .yw_setup $(RUN_OUTPUTS)
	$(YW_CMD) recon $(WORKFLOW_SCRIPT) $(YW_RECON_OPTIONS)

$(YW_VIEWS): $(YW_FACTS)
	$(BASH) $(SCRIPTS_DIR)/materialize_yw_views.sh > $(YW_VIEWS)

$(RUN_OUTPUTS): $(WORKFLOW_SCRIPT)
	$(SCRIPT_RUN_CMD) &> $(RUN_STDOUT)
	$(POST_RUN_CMD)

$(YW_DATA_GRAPH_GV): $(YW_VIEWS)
	mkdir -p graphs
	$(BASH) $(SCRIPTS_DIR)/$(YW_DATA_GRAPH).sh > $(YW_DATA_GRAPH_GV)

$(YW_PROCESS_GRAPH_GV): $(YW_VIEWS)
	mkdir -p graphs
	$(BASH) $(SCRIPTS_DIR)/$(YW_PROCESS_GRAPH).sh > $(YW_PROCESS_GRAPH_GV)

$(YW_COMBINED_GRAPH_GV): $(YW_VIEWS)
	mkdir -p graphs
	$(BASH)  $(SCRIPTS_DIR)/$(YW_COMBINED_GRAPH).sh > $(YW_COMBINED_GRAPH_GV)

$(YW_PROSPECTIVE_LINEAGE_GRAPH_GV): $(YW_VIEWS)
	mkdir -p graphs
	$(BASH) $(SCRIPTS_DIR)/$(YW_PROSPECTIVE_LINEAGE_GRAPH).sh \
		$(PROSPECTIVE_LINEAGE_DATA) \
		> $(YW_PROSPECTIVE_LINEAGE_GRAPH_GV)

$(QUERY_OUTPUTS): $(QUERY_SCRIPT) $(YW_VIEWS) $(RULES)
	$(BASH) $(QUERY_SCRIPT) > $(QUERY_OUTPUTS)

clean:
	rm -rf $(FACTS_DIR) $(GRAPHS_DIR) run *.xwam *.gv *.png *.pdf *.P *.txt $(RULES_DIR)/*.xwam .yw_setup $(CUSTOM_TARGETS) $(CUSTOM_CLEAN)

repl: $(YW_VIEWS)
	expect $(SCRIPTS_DIR)/start_xsb.tcl