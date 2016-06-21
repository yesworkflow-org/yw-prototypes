
RULES_DIR = ../rules
SCRIPTS_DIR = ../scripts

YW_EXTRACT_FACTS = facts/yw_extract_facts.P
YW_MODEL_FACTS = facts/yw_model_facts.P
YW_FACTS = $(YW_EXTRACT_FACTS) $(YW_MODEL_FACTS)
YW_VIEWS = yw_views.P
YW_MODEL_OPTIONS = -c extract.language=python \
                   -c extract.factsfile=$(YW_EXTRACT_FACTS) \
                   -c model.factsfile=$(YW_MODEL_FACTS) \
                   -c query.engine=xsb

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
YW_GRAPHS = $(YW_DATA_GRAPH).gv \
            $(YW_PROCESS_GRAPH).gv \
	        $(YW_COMBINED_GRAPH).gv \
	        $(YW_PROSPECTIVE_LINEAGE_GRAPH).gv

GRAPHS = $(YW_GRAPHS)
PNGS = $(GRAPHS:.gv=.png)
PDFS = $(GRAPHS:.gv=.pdf)

all: $(QUERY_OUTPUTS) $(GRAPHS)
run: $(RUN_OUTPUTS)
yw: $(YW_FACTS) $(YW_VIEWS) $(YW_GRAPHS)
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
.PHONY: $(YW_PROSPECTIVE_LINEAGE_GRAPH).gv
endif

$(YW_FACTS): $(WORKFLOW_SCRIPT)
ifdef YW_JAR
	mkdir -p facts
	java -jar $(YW_JAR) model $(WORKFLOW_SCRIPT) $(YW_MODEL_OPTIONS)
else
	$(error Must set YW_JAR environment variable to path to YesWorkflow jar)
endif

$(YW_VIEWS): $(YW_FACTS)
	bash $(SCRIPTS_DIR)/materialize_yw_views.sh > $(YW_VIEWS)

$(RUN_OUTPUTS): $(WORKFLOW_SCRIPT)
	$(SCRIPT_RUN_CMD) > $(RUN_STDOUT)
	${POST_RUN_CMD}

$(YW_DATA_GRAPH).gv: $(YW_VIEWS)
	bash $(SCRIPTS_DIR)/$(YW_DATA_GRAPH).sh > $(YW_DATA_GRAPH).gv

$(YW_PROCESS_GRAPH).gv: $(YW_VIEWS)
	bash $(SCRIPTS_DIR)/$(YW_PROCESS_GRAPH).sh > $(YW_PROCESS_GRAPH).gv

$(YW_COMBINED_GRAPH).gv: $(YW_VIEWS)
	bash $(SCRIPTS_DIR)/$(YW_COMBINED_GRAPH).sh > $(YW_COMBINED_GRAPH).gv

$(YW_PROSPECTIVE_LINEAGE_GRAPH).gv: $(YW_VIEWS)
	bash $(SCRIPTS_DIR)/$(YW_PROSPECTIVE_LINEAGE_GRAPH).sh \
		$(PROSPECTIVE_LINEAGE_DATA) \
		> $(YW_PROSPECTIVE_LINEAGE_GRAPH).gv

$(NW_FILTERED_LINEAGE_GRAPH).gv: $(NW_FACTS)
	now helper df_style.py
	now dataflow -v 55 -f $(RETROSPECTIVE_LINEAGE_VALUE) -m simulation | python df_style.py -d BT -e > $(NW_FILTERED_LINEAGE_GRAPH).gv

$(YW_NW_RETROSPECTIVE_LINEAGE_GRAPH).gv : $(YW_NW_VIEWS)
	bash $(SCRIPTS_DIR)/$(YW_NW_RETROSPECTIVE_LINEAGE_GRAPH).sh \
		$(RETROSPECTIVE_LINEAGE_DATA) $(RETROSPECTIVE_LINEAGE_VALUE) \
		> $(YW_NW_RETROSPECTIVE_LINEAGE_GRAPH).gv

$(QUERY_OUTPUTS): $(QUERY_SCRIPT) $(YW_VIEWS) $(NW_VIEWS) $(YW_NW_VIEWS) $(RULES)
	bash $(QUERY_SCRIPT) > $(QUERY_OUTPUTS)

clean:
	rm -rf facts .noworkflow *.xwam *.gv *.png *.pdf *.P *.txt  df_style.py $(RULES_DIR)/*.xwam

repl: $(YW_NW_VIEWS)
	expect $(RULES_DIR)/start_xsb.exp
