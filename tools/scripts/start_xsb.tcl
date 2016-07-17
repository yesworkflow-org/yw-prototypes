#!/usr/bin/env expect

set FACTS_DIR facts
set RULES_DIR ../../rules/xsb

spawn xsb --nobanner --nofeedback

expect "| ?- "

send {['facts/yw_extract_facts'].}
send \n
expect "| ?- "

send {['facts/yw_model_facts'].}
send \n
expect "| ?- "

send {['facts/yw_recon_facts'].}
send \n
expect "| ?- "

send {['facts/yw_views'].}
send \n
expect "| ?- "

send {['../../tools/rules/xsb/general_rules'].}
send \n
expect "| ?- "

send {['../../tools/rules/xsb/yw_rules'].}
send \n
expect "| ?- "

send {['../../tools/rules/xsb/yw_graph_rules'].}
send \n
expect "| ?- "

send {['../../tools/rules/xsb/gv_rules'].}
send \n
expect "| ?- "

send {['../../tools/rules/xsb/yw_graph_rules'].}
send \n
expect "| ?- "

interact

