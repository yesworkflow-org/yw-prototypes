#!/bin/bash
clear
echo ""


echo ""
echo "*** Extract Queries ***"
echo ""

echo "EQ1:  What source files were YW annotations extracted from?"
dlv -silent -pfilter=eq1 extractfacts.dlv extract_queries.dlv
echo ""

echo "EQ2:  What are the names of all program blocks?"
dlv -silent -pfilter=eq2 extractfacts.dlv extract_queries.dlv
echo ""

echo "EQ3:  What out ports are qualified with URIs?"
dlv -silent -pfilter=eq3 extractfacts.dlv extract_queries.dlv
echo ""


echo ""
echo "*** Model Queries ***"
echo ""

echo "MQ1:  Where is the definition of program simulate_data_collection.collect_data_set?"
dlv -silent -pfilter=mq1 extractfacts.dlv modelfacts.dlv rules.dlv model_queries.dlv
echo ""

echo "% MQ2:  What is the name of the top-level workflow?"
dlv -silent -pfilter=mq2 extractfacts.dlv modelfacts.dlv rules.dlv model_queries.dlv
echo ""

echo "MQ3:  What are the names of any top-level functions?"
dlv -silent -pfilter=mq3 extractfacts.dlv modelfacts.dlv rules.dlv model_queries.dlv
echo ""

echo "MQ4:  What are the names of the programs comprising the top-level workflow?"
dlv -silent -pfilter=mq4 extractfacts.dlv modelfacts.dlv rules.dlv model_queries.dlv
echo ""

echo "MQ5:  What are the inputs to the top-level workflow?"
dlv -silent -pfilter=mq5 extractfacts.dlv modelfacts.dlv rules.dlv model_queries.dlv
echo ""

echo "MQ6:  What are the outputs of program block simulate_data_collection.collect_data_set?"
dlv -silent -pfilter=mq6 extractfacts.dlv modelfacts.dlv rules.dlv model_queries.dlv
echo ""

echo "MQ7: What program blocks provide input directly to simulate_data_collection.collect_data_set?"
dlv -silent -pfilter=mq7 extractfacts.dlv modelfacts.dlv rules.dlv model_queries.dlv
echo ""

echo "MQ8: What programs have input ports that receive data simulate_data_collection[cassette_id]?"
dlv -silent -pfilter=mq8 extractfacts.dlv modelfacts.dlv rules.dlv model_queries.dlv
echo ""

echo "MQ9: How many ports read data simulate_data_collection[frame_number]?"
dlv -silent -pfilter=mq9 extractfacts.dlv modelfacts.dlv rules.dlv model_queries.dlv
echo ""

echo "MQ10: How many data are read by more than port in workflow simulate_data_collection?"
dlv -silent -pfilter=data_read_by_multiple_ports extractfacts.dlv modelfacts.dlv rules.dlv model_queries.dlv
echo ""

echo "MQ11: What program blocks are immediately downstream of calculate_strategy?"
dlv -silent -pfilter=mq11 extractfacts.dlv modelfacts.dlv rules.dlv model_queries.dlv
echo ""

echo "MQ12: What program blocks are immediately upstream of transform_images?"
dlv -silent -pfilter=mq12 extractfacts.dlv modelfacts.dlv rules.dlv model_queries.dlv
echo ""

echo "MQ13: What program blocks are upstream of transform_images?"
dlv -silent -pfilter=mq13 extractfacts.dlv modelfacts.dlv rules.dlv model_queries.dlv
echo ""

echo "MQ14: What program blocks are anywhere downstream of calculate_strategy?"
dlv -silent -pfilter=mq14 extractfacts.dlv modelfacts.dlv rules.dlv model_queries.dlv
echo ""

echo "MQ15: What data is immediately downstream of raw_image?"
dlv -silent -pfilter=mq15 extractfacts.dlv modelfacts.dlv rules.dlv model_queries.dlv
echo ""

echo "MQ16: What data is immediately upstream of raw_image?"
dlv -silent -pfilter=mq16 extractfacts.dlv modelfacts.dlv rules.dlv model_queries.dlv
echo ""

echo "MQ17: What data is downstream of accepted_sample?"
dlv -silent -pfilter=mq17 extractfacts.dlv modelfacts.dlv rules.dlv model_queries.dlv
echo ""

echo "MQ18: What data is upstream of raw_image?"
dlv -silent -pfilter=mq18 extractfacts.dlv modelfacts.dlv rules.dlv model_queries.dlv
echo ""

echo "MQ19: What URI variables are associated with writes of data simulate_data_collection[corrected_image]?"
dlv -silent -pfilter=mq19 extractfacts.dlv modelfacts.dlv rules.dlv model_queries.dlv
echo ""