#!/bin/bash
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

echo "MQ2:  What is the name of the top-level workflow?"
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
dlv -silent -pfilter=mq10 extractfacts.dlv modelfacts.dlv rules.dlv model_queries.dlv
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

echo "MQ20: What URI variables do data written to raw_image and corrected_image have in common?"
dlv -silent -pfilter=mq20 extractfacts.dlv modelfacts.dlv rules.dlv model_queries.dlv
echo ""


echo ""
echo "*** Reconstructed Trace Queries ***"
echo ""

echo "RQ0: What URI variable values are associated with writing resource run/data/DRT322/DRT322_11000eV_028.img?"
dlv -silent -pfilter=rq0 extractfacts.dlv modelfacts.dlv reconfacts.dlv rules.dlv recon_queries.dlv
echo ""

echo "RQ1: What samples did the run of the script collect images from?"
dlv -silent -pfilter=rq1 extractfacts.dlv modelfacts.dlv reconfacts.dlv rules.dlv recon_queries.dlv
echo ""

echo "RQ2: What energies were used during collection of images from sample DRT322?"
dlv -silent -pfilter=rq2 extractfacts.dlv modelfacts.dlv reconfacts.dlv rules.dlv recon_queries.dlv
echo ""

echo "RQ3: Where is the raw image from which corrected image run/data/DRT322/DRT322_11000eV_028.img is derived?"
dlv -silent -pfilter=rq3 extractfacts.dlv modelfacts.dlv reconfacts.dlv rules.dlv recon_queries.dlv
echo ""

echo "RQ4: Are there any raw images for which there are no corresponding corrected images?"
dlv -silent -pfilter=rq4 extractfacts.dlv modelfacts.dlv reconfacts.dlv rules.dlv recon_queries.dlv
echo ""

echo "RQ5: Where is the spreadsheet that led to the corrected image run/data/DRT240/DRT240_10000eV_010.img?"
dlv -silent -pfilter=rq5 extractfacts.dlv modelfacts.dlv reconfacts.dlv rules.dlv recon_queries.dlv
echo ""