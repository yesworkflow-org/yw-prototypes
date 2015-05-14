## AffyGeneExpressionAnalysisV12.R
## Version 12, Monday, January 12th, 2015.
## Created By: Tyler Kolisnik and Mark Bieda.

## Description: This is a program to analyze gene expression microarray data from Affymetrics Platforms. 
# It normalizes array data, generates differentially expressed genes, produces a heatmap, and performs a Gene Ontology Analysis. 

## Run Time: Dependent on size of input files, but usually no more than 5 minutes. 

## This program uses the YesWorkflow Scientific Workflow Management System.

## In order to run this program, the following must be done:
# 1. .CEL files from the microarray data must be in the inputDirectory.
# 2. A Covariant Description (covdesc) file is required, it must be in the inputDirectory.
# This file name must be covdesc with no extension.
# It must be tab-separated, and with linux line endings (\n).
# Important Note: First line of file has a tab, then conditionName, (conditionName corresponds to the column with ns and ad in the example below)
# The left column corresponds to the inputted .CEL file names, the right column corresponds to the conditions of the type of experiment recorded in the .CEL file. 
# In this example, we chose to label our conditions ns for neurosphere-like and ad for adherent cell types, they can be labeled however you want.

## Example file:
# conditionName
#GSM172065.CEL	ns
#GSM172064.CEL	ns
#GSM172063.CEL	ns
#GSM172067.CEL	ad
#GSM172066.CEL	ad
#GSM170898.CEL	ad

# 3. Libraries must be installed in R. See the bioconductor website for installation information. 
# 4. Parameters within the Parameter Block must be changed to match the parameters you wish to use. 
# 5. Run the program.



## Dependencies: 
library(simpleaffy) # Required for data normalization.
library(marray) # Required for Heatmap Generation.
library(gplots) # Required for Heatmap Generation.
library(limma) # Required for DEG (Differentially Expressed Gene) Selection.
library(hgu133plus2.db) # Must be changed if using a different array other than Affymetrix hgu133plus2.0 array. Other points in code must be changed as well.
library(RColorBrewer) # Required for Heatmap Generation.
library(org.Hs.eg.db) # Organism Database ID (used by GOstats), must be changed if using an organism other than human (default).
library(GOstats) # Required for Gene Ontology Analysis.

## @begin AffyGeneExpressionAnalysis
# @in inputDirectory @as CEL_and_covdesc_Directory
# @param minFC @as minimum_fold_change_for_DEG
# @param ttestPVal @as cutoff_p_value_for_DEG
# @param hgCutoff @as GO_stats_p_value_cutoff
# @out exprset @as normalized_data_only_values
# @out exprsetlinkedtogenes @as DEG_list_full_info
# @out idrlinkedtogenes @as DEG_list_summary
# @out higheridrlinkedtogenes @as DEG_list_higher_in_test_condition
# @out loweridrlinkedtogenes @as DEG_list_lower_in_test_condition
# @out pdfOutputFile @as heatmap
# @out gostatshigher @as GO_stats_gene_list_higher_in_test_condition
# @out BP_SummH_File @as GO_stats_BP_higher_in_test_condition
# @out CC_SummH_File @as GO_stats_CC_higher_in_test_condition
# @out MF_SummH_File @as GO_stats_MF_higher_in_test_condition
# @out gostatslower @as GO_stats_gene_list_lower_in_test_condition
# @out BP_SummL_File @as GO_stats_BP_lower_in_test_condition
# @out CC_SummL_File @as GO_stats_CC_lower_in_test_condition
# @out MF_SummL_File @as GO_stats_MF_lower_in_test_condition


##################################################################### Begin Parameter Block #####################################################################

inputDirectory <- "/home/user/Documents/celDIRECTORY" # Path of directory where input is located, must contain .CEL files and a covdesc file, no trailing /.
outputDirectory <- "/home/user/Documents/outputDIRECTORY" # Path of directory where output will be located, no trailing /.
runName <- "AffyTest1" # A string that will be appended to all output files.
baseline <- "ad" # This is a string depicting the baseline condition from the covdesc file (like a control condition). It must match a condition in the covdesc file.
minFC <- 1.5 # This is the minimum log2 fold change for a gene to be differentially expressed.
ttestPVal <- 0.01 # This is the threshold p value for significance. 
hgCutoff <- 0.01 # This is the GOStats p value threshold.

##################################################################### End Parameter Block #######################################################################

##################################################################### Begin Coding Block #####################################################################

## @begin Normalize
# @in inputDirectory @as CEL_and_covdesc_Directory
# @out exprset @as normalized_data_only_values
# @out dataSet @as normalized_data

########################### Begin Normalization Block #########################

options(max.print=100)
setwd(inputDirectory)
dataSet <- justRMA(phenoData="covdesc") # The input data is normalized using the justRMA function from the simpleaffy package here.
pdSet <- pData(dataSet)
pdSet[,"blankcol"] <- "c"
pData(dataSet) <- pdSet
exprset <- exprs(dataSet) 
conditions <- pData(dataSet)$conditionName
uniqueConditions <- unique(conditions)
mytestcond <- uniqueConditions[uniqueConditions !=baseline]
normalizedDataName <- paste(outputDirectory,"/", runName, "_ALLnormalized_", mytestcond[1], "_vs_",baseline, ".txt", sep="") # Sets file name.
write.table(exprset, file=normalizedDataName, row.names=FALSE, quote=FALSE,sep="\t")

########################### End Normalization Block ##########################

## @end Normalize

################ Selection of Differentially Expressed Genes ################

## @begin SelectDEGs
# @in dataSet @as normalized_data
# @param minFC @as minimum_fold_change_for_DEG
# @param ttestPVal @as cutoff_p_value_for_DEG
# @out exprsetlinkedtogenes @as DEG_list_full_info
# @out idrlinkedtogenes @as DEG_list_summary
# @out higheridrlinkedtogenes @as DEG_list_higher_in_test_condition
# @out loweridrlinkedtogenes @as DEG_list_lower_in_test_condition
# @out diffresults @as DEG_list


# IMPORTANT: This code is only for experiments in which only two conditions being compared,
# In this case, conditionName has only TWO levels (e.g.: control, drug).

## Limma DEG calculations are done here:
gc()
pData(dataSet) <- pdSet
mypd <-pData(dataSet)
f <- factor(mypd$conditionName, levels=uniqueConditions)
design <- model.matrix(~0+f)
colnames(design) <- uniqueConditions
fit <- lmFit(dataSet, design)
mycontrast <- paste(mytestcond,"-", baseline)
contrast.matrix <- makeContrasts(mycontrast,levels=design)
fit2 <- contrasts.fit(fit, contrast.matrix)
fit2 <- eBayes(fit2)

# Create the Limma-Differentially Expressed Gene List Linked to Symbols.
diffresults <- topTable(fit2, number=100000,coef=1, adjust="none", p.value=ttestPVal, lfc=log2(minFC))
limmaDEGlist <- exprset[rownames(diffresults),]
limmaDEGlistSymbols <- merge(limmaDEGlist, linkedprobes, by.x=0, by.y="PROBEID")
ids <- rownames(exprset)
# In the next line the ChipDb package from Bioconductor is hgu133plus2.db which corresponds to the hgu133plus2.0 chip, this must be changed for other arrays and in other places in the code.
linkedprobes <- select(hgu133plus2.db, keys= ids, columns = "SYMBOL", keytype = "PROBEID") # If using an exon array, set keytype="PMID".
exprsetlinkedtogenes <- merge(exprset, linkedprobes, by.x=0, by.y="PROBEID")
col_1st_exprset <- grep("SYMBOL", names(exprsetlinkedtogenes)) # Changes Column Order.
exprsetlinkedtogenes <- exprsetlinkedtogenes[,c(col_1st_exprset, (1:ncol(exprsetlinkedtogenes))[-col_1st_exprset])]
colnames(exprsetlinkedtogenes)[2] <- "Probeset Ids"
f1name <- paste(outputDirectory,"/", runName, "_exprsetlinkedtogenes_", mytestcond[1], "_vs_",baseline, ".txt", sep="") # Sets file name.
write.table(exprsetlinkedtogenes, file=f1name, row.names=FALSE, quote=FALSE,sep="\t")

# This outputs the DEG list with the logFC, pvalue, and adjusted p value.
gc()
importantdiffresults <- diffresults[,(c(1,4,5))]
idrlinkedtogenes <- merge(importantdiffresults, linkedprobes, by.x=0, by.y="PROBEID")
col_1st <- grep("SYMBOL", names(idrlinkedtogenes)) # Changes Column Order.
idrlinkedtogenes <- idrlinkedtogenes[,c(col_1st, (1:ncol(idrlinkedtogenes))[-col_1st])] # Changes Column Order.
colnames(idrlinkedtogenes)[2]<- "ProbesetIds" # Gives more detailed Column Titles.
fname <- paste(outputDirectory, "/", runName, "_", "LimmaDEGlist_",mytestcond[1],"_vs_",baseline,".txt",sep="") # Sets file name.
write.table(idrlinkedtogenes,file=fname,row.names=FALSE,quote=FALSE,sep="\t") 
higheridrlinkedtogenes<-subset(idrlinkedtogenes,idrlinkedtogenes[,3]>0)
hfname <- paste(outputDirectory, "/", runName, "_", conditions[1],"_LimmaHigher_",mytestcond[1],"_vs_",baseline,".txt",sep="") # Sets file name.
write.table(higheridrlinkedtogenes,file=hfname,row.names=FALSE,quote=FALSE,sep="\t")
loweridrlinkedtogenes<-subset(idrlinkedtogenes,idrlinkedtogenes[,3]<0)
lfname <- paste(outputDirectory, "/", runName, "_", conditions[1],"_LimmaLower_",mytestcond[1],"_vs_",baseline,".txt",sep="") # Sets file name.
write.table(loweridrlinkedtogenes,file=lfname,row.names=FALSE,quote=FALSE,sep="\t")

################ End Selection of Differentially Expressed Genes ############

## @end SelectDEGs

## @begin MakeHeatmap
# @in diffresults @as DEG_list
# @out pdfOutputFile @as heatmap

########################### Begin Heatmap Block #########################

## The heatmap is generated here.
pdfOutputFile <- paste(outputDirectory, "/", runName, "_heatmap.pdf", sep="") # Sets file name.
pdf(pdfOutputFile)
my_palette <- colorRampPalette(c("blue", "black", "red"))(n = 299)
heatmap.2(exprset[rownames(diffresults),], rowInd=NULL,col=my_palette,labRow=NA,scale="row",cexCol=0.5,key=TRUE, symkey=FALSE, density.info="none", trace="none")
dev.off()

gc()

########################### End Heatmap Block ##########################

## @end MakeHeatmap


## @begin GO_Analysis
# @param hgCutoff @as GO_stats_p_value_cutoff
# @in higheridrlinkedtogenes @as DEG_list_higher_in_test_condition
# @in loweridrlinkedtogenes @as DEG_list_lower_in_test_condition
# @out gostatshigher @as GO_stats_gene_list_higher_in_test_condition
# @out BP_SummH_File @as GO_stats_BP_higher_in_test_condition
# @out CC_SummH_File @as GO_stats_CC_higher_in_test_condition
# @out MF_SummH_File @as GO_stats_MF_higher_in_test_condition
# @out gostatslower @as GO_stats_gene_list_lower_in_test_condition
# @out BP_SummL_File @as GO_stats_BP_lower_in_test_condition
# @out CC_SummL_File @as GO_stats_CC_lower_in_test_condition
# @out MF_SummL_File @as GO_stats_MF_lower_in_test_condition

########################### Begin GOStats Block #########################

## Gene Ontology Statistics are Calculated Here.

# Gene Ontology Categories that were shown to be relatively Higher (more expressed) in the Experimental Condition.
gostatshigher <- higheridrlinkedtogenes[1]
higherstatsfilename <- paste(outputDirectory, "/", runName, "_", conditions[1],"_GOStatsHigher_",mytestcond[1], "_vs_",baseline,".txt",sep="") # Sets file name.
write.table(gostatshigher,file=higherstatsfilename, row.names=FALSE, col.names=FALSE, quote=FALSE, sep="\t") 
geneListHigherCHR <- gostatshigher$SYMBOL
geneListHigherLinkedtoEntrezIds <- select(hgu133plus2.db, keys= geneListHigherCHR, "ENTREZID", "SYMBOL") 
GOstatsGenesH <- geneListHigherLinkedtoEntrezIds[,2]

x <- org.Hs.egACCNUM
mapped_genes <- mappedkeys(x)
xx <- as.list(x[mapped_genes])
geneUniverse <- (unique(names(xx)))


paramsBPH <- new("GOHyperGParams", geneIds=GOstatsGenesH, universeGeneIds=geneUniverse,
annotation="org.Hs.eg.db", ontology="BP", pvalueCutoff=hgCutoff, conditional=TRUE,
testDirection="over")

paramsCCH <- new("GOHyperGParams", geneIds=GOstatsGenesH, universeGeneIds=geneUniverse,
annotation="org.Hs.eg.db", ontology="CC", pvalueCutoff=hgCutoff, conditional=TRUE,
testDirection="over")

paramsMFH <- new("GOHyperGParams", geneIds=GOstatsGenesH, universeGeneIds=geneUniverse,
annotation="org.Hs.eg.db", ontology="MF", pvalueCutoff=hgCutoff, conditional=TRUE,
testDirection="over")

BP_GOdataH <- hyperGTest(paramsBPH) # Biological Process.
BP_SummH <- summary(BP_GOdataH)
CC_GOdataH <- hyperGTest(paramsCCH) # Cellular Component.
CC_SummH <- summary(CC_GOdataH)
MF_GOdataH <- hyperGTest(paramsMFH) # Molecular Function.
MF_SummH <- summary(MF_GOdataH)

BP_SummH_File <- paste(outputDirectory, "/", runName, "_", conditions[1],"_higher_BP_GOStats.txt",sep="") # Sets file name.
write.table(BP_SummH,file=BP_SummH_File,row.names=FALSE,quote=FALSE,col.names=TRUE, sep="\t")
CC_SummH_File <- paste(outputDirectory, "/", runName, "_", conditions[1],"_higher_CC_GOStats.txt",sep="") # Sets file name.
write.table(CC_SummH,file=CC_SummH_File,row.names=FALSE,quote=FALSE,col.names=TRUE, sep="\t")
MF_SummH_File <- paste(outputDirectory, "/", runName, "_", conditions[1],"_higher_MF_GOStats.txt",sep="") # Sets file name.
write.table(MF_SummH,file=MF_SummH_File,row.names=FALSE,quote=FALSE,col.names=TRUE, sep="\t")


# Gene Ontology Categories that were shown to be relatively lower (less expressed) in Experimental Condition.

gostatslower <- loweridrlinkedtogenes[1]
lowerstatsfilename <- paste(outputDirectory, "/", runName, "_", conditions[1],"_GOStatsLower_", mytestcond[1],"_vs_",baseline,".txt",sep="") # Sets file name.
write.table(gostatslower,file=lowerstatsfilename,row.names=FALSE,col.names=FALSE,quote=FALSE,sep="\t") 
geneListLowerCHR <- gostatslower$SYMBOL
geneListLowerLinkedtoEntrezIds <- select(hgu133plus2.db, keys= geneListLowerCHR, "ENTREZID", "SYMBOL")
GOstatsGenesL <- geneListLowerLinkedtoEntrezIds[,2]


paramsBPL <- new("GOHyperGParams", geneIds=GOstatsGenesL, universeGeneIds=geneUniverse,
annotation="org.Hs.eg.db", ontology="BP", pvalueCutoff=hgCutoff, conditional=TRUE,
testDirection="over")

paramsCCL <- new("GOHyperGParams", geneIds=GOstatsGenesL, universeGeneIds=geneUniverse,
annotation="org.Hs.eg.db", ontology="CC", pvalueCutoff=hgCutoff, conditional=TRUE,
testDirection="over")

paramsMFL <- new("GOHyperGParams", geneIds=GOstatsGenesL, universeGeneIds=geneUniverse,
annotation="org.Hs.eg.db", ontology="MF", pvalueCutoff=hgCutoff, conditional=TRUE,
testDirection="over")

BP_GOdataL <- hyperGTest(paramsBPL) # Biological Process. 
BP_SummL <- summary(BP_GOdataL)
CC_GOdataL <- hyperGTest(paramsCCL) # Cellular Component.
CC_SummL <- summary(CC_GOdataL)
MF_GOdataL <- hyperGTest(paramsMFL) # Molecular Function.
MF_SummL <- summary(MF_GOdataL)

BP_SummL_File <- paste(outputDirectory, "/", runName, "_", conditions[1],"_lower_BP_GOStats.txt",sep="") # Sets file name.
write.table(BP_SummL,file=BP_SummL_File, row.names=FALSE,quote=FALSE,col.names=TRUE, sep="\t")
CC_SummL_File <- paste(outputDirectory, "/", runName,"_", conditions[1],"_lower_CC_GOStats.txt",sep="") # Sets file name.
write.table(CC_SummL,file=CC_SummL_File, row.names=FALSE,quote=FALSE,col.names=TRUE, sep="\t")
MF_SummL_File <- paste(outputDirectory, "/", runName, "_", conditions[1],"_lower_MF_GOStats.txt",sep="") # Sets file name.
write.table(MF_SummL,file=MF_SummL_File,row.names=FALSE,quote=FALSE,col.names=TRUE, sep="\t")

########################### End GOStats Block #########################

##################################################################### End Coding Block #####################################################################

## @end GO_Analysis
## @end AffyGeneExpressionAnalysis