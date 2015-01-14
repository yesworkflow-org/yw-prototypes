## @begin CAR_Recon
# @in MASTER.DATA @as master_data_directory
# @in PRISM @as prism_directory
# @in calibration.years @as calibration_years
# @in retrodiction.years @as retrodiction_years
# @in ITRDB.data @as tree_ring_data
# @out ZuniCibola_PRISM_grow_prcp_ols_loocv_union_recons.tif @as ZuniCibola_PRISM_grow_prcp_ols_loocv_union_recons.tif
# @out ZuniCibola_PRISM_grow_prcp_ols_loocv_union_errors.tif @as ZuniCibola_PRISM_grow_prcp_ols_loocv_union_errors.tif

######## BEGIN PREAMBLE
# Load the functions for all analyses below
# install.packages("devtools")
# devtools::install_github("cran/wild1")
# devtools::install_github("bocinsky/FedData")
library(FedData)
pkgTest("care")
pkgTest("forecast")

source('./OLS_LOOCV_CUSTOM_FUNCTIONS.R')

# Suppress use of scientific notation
options(scipen=999)

# Create an output directory above the R script directory
dir.create("../OUTPUT/", showWarnings=F)

######## END PREAMBLE


## [INTENTIONALLY LEFT BLANK]


######## BEGIN PARAMETERS
## Set the master data directory
## This should be the location of PRISM data
MASTER.DATA <- "/Volumes/BOCINSKY_DATA/DATA/"


## Set the calibration period
# Here, I use a 60 year period ending at 1983 
# to maximize the number of dendro series.
calibration.years <- 1924:1983
# calibration.years <- 1910:1919

## Set the retrodiction years
# Here, we are setting it for 1--2000,
# for consistency with Bocinsky & Kohler 2014
retrodiction.years <- 1:2000

# Force Raster to load large rasters into memory
rasterOptions(chunksize=2e+07,maxmemory=2e+08)


######## END PARAMETERS


## [INTENTIONALLY LEFT BLANK]

## @begin GetModernClimate
# @in MASTER.DATA @as master_data_directory
# @in PRISM @as prism_directory
# @out ZuniCibola.PRISM.monthly.prcp @as PRISM_monthly_net_precipitation
# @out ZuniCibola.PRISM.grow.prcp @as PRISM_annual_growing_season_precipitation
######## BEGIN DATA IMPORT AND PREPROCESSING
## Load the PRISM (interpolated climate) data for each of the study regions
## ZUNI-CIBOLA
## Define the Zuni/Cibola study areas.
ZuniCibola.polygon <- readOGR("../DATA/LTVTP_case_studies/Zuni_Cibola_Shapefile", layer='ZuniBoundary')
# Growing-season precipitation
ZuniCibola.PRISM.monthly.prcp <- getPRISM_MONTHLYData(template=ZuniCibola.polygon, type='ppt', out.dir=paste(MASTER.DATA,"PRISM/EXTRACTIONS/", sep=''), monthly.dir=paste(MASTER.DATA,"PRISM/LT81_800M/",sep=''), label="LTVTP_Zuni_Cibola", force.redo=F)
# Extract the net precipitation over the growing season, May--September
ZuniCibola.PRISM.grow.prcp <- annualizePRISM_MONTHLY(prism.brick=ZuniCibola.PRISM.monthly.prcp, months=c(5,6,7,8,9), fun="sum")
# Generate annual layer names
ZuniCibola.PRISM.grow.prcp <- subset(ZuniCibola.PRISM.grow.prcp,grep(paste(calibration.years,collapse="|"),names(ZuniCibola.PRISM.grow.prcp)))
writeRaster(ZuniCibola.PRISM.grow.prcp,"../OUTPUT_FINAL/ZuniCibola_PRISM_grow_prcp.tif", datatype="FLT4S", options=c("COMPRESS=DEFLATE", "ZLEVEL=9", "INTERLEAVE=BAND"), overwrite=T, setStatistics=FALSE)
# Clean-up
rm(ZuniCibola.PRISM.monthly.prcp)
gc()
gc()

## @end GetModernClimate

#### TEMPORARY: Select smaller sub-area for testing
ZuniCibola.PRISM.grow.prcp <- brick("../OUTPUT_FINAL/ZuniCibola_PRISM_grow_prcp.tif")
ZuniCibola.PRISM.grow.prcp.test <- crop(ZuniCibola.PRISM.grow.prcp,extent(c(-109,-108.92,34,34.03)))
names(ZuniCibola.PRISM.grow.prcp.test) <- calibration.years

## @begin SubsetAllData
# @in ZuniCibola.PRISM.grow.prcp @as PRISM_annual_growing_season_precipitation
# @in ITRDB.data @as tree_ring_data
# @in calibration.years @as calibration_years
# @in retrodiction.years @as retrodiction_years
# @out training.series @as dendro_series_for_calibration
# @out recon.series @as dendro_series_for_reconstruction
# @out predlist @as list_of_predictor_series_through_time

## Load the ITRDB (tree ring) data, and crop to study period
# if(file.exists(paste(MASTER.DATA,"DENDRO/ITRDB/EXTRACTIONS/ITRDB_DATA.csv", sep=''))){
  # Get a SPDF of ITRDB metadata and get ITRDB database
  ITRDB.data <- read.csv("../DATA/DENDRO/ITRDB/EXTRACTIONS/ITRDB_DATA.csv")
#   ITRDB.meta.sp <- getITRDBMetadataSPDF(names(ITRDB.data),data.dir="../DATA/DENDRO/ITRDB/EXTRACTIONS/")
# }else{
#   ITRDB.data <- getITRDB(raw.dir = paste(MASTER.DATA,"DENDRO/ITRDB/",sep=''), output.dir="~/Desktop/TEST/", type='standard', download=F, force.redo=T)
#   ITRDB.meta <- read.csv(paste(MASTER.DATA,"DENDRO/ITRDB/EXTRACTIONS/ITRDB_METADATA.csv", sep=''),colClasses="character")
#   
#   # Get a SPDF of ITRDB metadata
#   ITRDB.meta.sp <- getITRDBMetadataSPDF(names(ITRDB.data),data.dir=paste(MASTER.DATA,"DENDRO/ITRDB/EXTRACTIONS/", sep=''))
#   
# #   # Get a shapefile of the 48 CONUS states
#   states <- readOGR(paste(MASTER.DATA,"NATIONAL_ATLAS/statep010",sep=''), layer='statep010')
#   # And select only the four corners
#   states <- states[states$STATE %in% c("Arizona","Colorado","Utah","New Mexico"),]
#   states <- spTransform(states,CRS(projection(ITRDB.meta.sp)))
# #   
# #   # Trim the IRTB database to the four corners states
# #   ITRDB.meta.sp <- ITRDB.meta.sp[as.vector(!is.na((ITRDB.meta.sp %over% states)[,1])),]
#   # Select only the four corners series
#   ITRDB.data <- ITRDB.data[,c("YEAR",as.character(ITRDB.meta.sp$SERIES))]
#   # Remove years with no data
#   ITRDB.data <- ITRDB.data[apply(ITRDB.data,1,FUN=function(i){any(!is.na(i[-1]))}),]
#   # Clean up the chronology names
#   ITRDB.meta.sp$NAME <- sanitizeITRDBnames(ITRDB.meta.sp$NAME)
#   
#   # Write the amended ITRDB data and metadata
#   write.csv(ITRDB.data,paste(MASTER.DATA,"DENDRO/ITRDB/EXTRACTIONS/ITRDB_DATA.csv", sep=''),row.names=F)
#   write.csv(ITRDB.meta.sp,paste(MASTER.DATA,"DENDRO/ITRDB/EXTRACTIONS/ITRDB_METADATA.csv", sep=''), row.names=F)
# }

######## FINISH DATA IMPORT AND PREPROCESSING


## [INTENTIONALLY LEFT BLANK]


######## BEGIN ANALYSIS
#### Spatial retrodictions of all climate signals
## Isolating the calibration years for slm coefficient estimation
training.series <- ITRDB.data[ITRDB.data$YEAR %in% calibration.years,]
training.series <- training.series[,t(complete.cases(t(training.series)))]
training.series <- training.series[,-1]

## Get list of periods in retrodiction years with stable chronologies, and list chronologies
retro.series <- ITRDB.data[ITRDB.data$YEAR %in% retrodiction.years,]
retro.series <- retro.series[,c("YEAR",names(training.series))]
rownames(retro.series) <- retro.series$YEAR
retro.series <- retro.series[,-1]
retro.series[!is.na(retro.series)] <- TRUE
retro.series[is.na(retro.series)] <- FALSE
retro.series <- unique(retro.series)
predlist <- lapply(1:nrow(retro.series),function(i){which(retro.series[i,]==1)})
names(predlist) <- rownames(retro.series)
retro.series.count <- rowSums(retro.series)

recon.series <- ITRDB.data[ITRDB.data$YEAR %in% retrodiction.years,]
recon.series <- recon.series[,c("YEAR",names(training.series))]
rownames(recon.series) <- recon.series$YEAR
# breaks <- c(as.numeric(rownames(retro.series)),(tail(retrodiction.years,1)+1))
# breaks.lengths <- diff(breaks)
# retro.series.list <- lapply(1:(length(breaks)-1),function(i){recon.series[as.character(breaks[i]):as.character(breaks[i+1]-1),-1,drop=F]})

## @end SubsetAllData



## @begin CAR_Analysis_unique
# @in ZuniCibola.PRISM.grow.prcp @as PRISM_annual_growing_season_precipitation
# @in training.series @as dendro_series_for_calibration
# @out ZuniCibola.PRISM.grow.prcp.models.unique @as cellwise_unique_selected_linear_models
system.time(ZuniCibola.PRISM.grow.prcp.models.unique <- ols.brick.unique(Xtrain=training.series, Y.brick=ZuniCibola.PRISM.grow.prcp.test, predlist=predlist, name="ZuniCibola_PRISM_grow_prcp_ols_loocv_TEST", select.stat="AICc", force.redo=T))
# ZuniCibola.PRISM.grow.prcp.models.unique <- readRDS("../OUTPUT/ZuniCibola_PRISM_grow_prcp_ols_loocv_selected_lms_unique.rds")
# system.time(ZuniCibola.PRISM.grow.prcp.recons.unique <- recon(name="ZuniCibola_PRISM_grow_prcp_ols_loocv_unique", models=ZuniCibola.PRISM.grow.prcp.models.unique, Ytrain.brick=ZuniCibola.PRISM.grow.prcp, recon.series=recon.series, training.series=training.series, force.redo=T))
# system.time(ZuniCibola.PRISM.grow.prcp.errors.unique <- recon.errors(name="ZuniCibola_PRISM_grow_prcp_ols_loocv_unique", models=ZuniCibola.PRISM.grow.prcp.models.unique, Ytrain.brick=ZuniCibola.PRISM.grow.prcp, select.stat="CV", recon.series=recon.series, force.redo=T))
## @end CAR_Analysis_unique

## @begin CAR_Analysis_union
# @in ZuniCibola.PRISM.grow.prcp @as PRISM_annual_growing_season_precipitation
# @in training.series @as dendro_series_for_calibration
# @in ZuniCibola.PRISM.grow.prcp.models.unique @as cellwise_unique_selected_linear_models
# @out ZuniCibola.PRISM.grow.prcp.models.union @as cellwise_union_selected_linear_models
system.time(ZuniCibola.PRISM.grow.prcp.models.union <- ols.brick.union(models=ZuniCibola.PRISM.grow.prcp.models.unique, Xtrain=training.series, Y.brick=ZuniCibola.PRISM.grow.prcp.test, name="ZuniCibola_PRISM_grow_prcp_ols_loocv_TEST", force.redo=T))
## @end CAR_Analysis_union

## @begin CAR_Reconstruction_union
# @in ZuniCibola.PRISM.grow.prcp @as PRISM_annual_growing_season_precipitation
# @in training.series @as dendro_series_for_calibration
# @in ZuniCibola.PRISM.grow.prcp.models.union @as cellwise_union_selected_linear_models
# @in recon.series @as dendro_series_for_reconstruction
# @out ZuniCibola.PRISM.grow.prcp.recons.union @as raster_brick_spatial_reconstruction
# @out ZuniCibola.PRISM.grow.prcp.errors.union @as raster_brick_spatial_reconstruction_errors
system.time(ZuniCibola.PRISM.grow.prcp.recons.union <- recon(name="ZuniCibola_PRISM_grow_prcp_ols_loocv_union_TEST", models=ZuniCibola.PRISM.grow.prcp.models.union, Ytrain.brick=ZuniCibola.PRISM.grow.prcp.test, recon.series=recon.series, training.series=training.series, force.redo=T))
system.time(ZuniCibola.PRISM.grow.prcp.errors.union <- recon.errors(name="ZuniCibola_PRISM_grow_prcp_ols_loocv_union_TEST", models=ZuniCibola.PRISM.grow.prcp.models.union, Ytrain.brick=ZuniCibola.PRISM.grow.prcp.test, select.stat="CV", recon.series=recon.series, force.redo=T))
## @end CAR_Reconstruction_union

## @begin CAR_Reconstruction_union_output
# @in ZuniCibola.PRISM.grow.prcp.recons.union @as raster_brick_spatial_reconstruction
# @in ZuniCibola.PRISM.grow.prcp.errors.union @as raster_brick_spatial_reconstruction_errors
# @out ZuniCibola_PRISM_grow_prcp_ols_loocv_union_recons.tif @as ZuniCibola_PRISM_grow_prcp_ols_loocv_union_recons.tif
# @out ZuniCibola_PRISM_grow_prcp_ols_loocv_union_errors.tif @as ZuniCibola_PRISM_grow_prcp_ols_loocv_union_errors.tif
ZuniCibola.PRISM.grow.prcp.recons.union <- calc(ZuniCibola.PRISM.grow.prcp.recons.union,function(x){x[x<0] <- 0; return(x)})
writeRaster(ZuniCibola.PRISM.grow.prcp.recons.union,"../OUTPUT_FINAL/ZuniCibola_PRISM_grow_prcp_ols_loocv_union_recons.tif", datatype="FLT4S", options=c("COMPRESS=DEFLATE", "ZLEVEL=9", "INTERLEAVE=BAND"), overwrite=T, setStatistics=FALSE)
writeRaster(ZuniCibola.PRISM.grow.prcp.errors.union,"../OUTPUT_FINAL/ZuniCibola_PRISM_grow_prcp_ols_loocv_union_errors.tif", datatype="FLT4S", options=c("COMPRESS=DEFLATE", "ZLEVEL=9", "INTERLEAVE=BAND"), overwrite=T, setStatistics=FALSE)
## @end CAR_Reconstruction_union_output

## @end CAR_Recon

# plot(cellStats(ZuniCibola.PRISM.grow.prcp.recons.union,mean), type='l')
# recon.rast <- brick("../OUTPUT_FINAL/ZuniCibola_PRISM_grow_prcp_ols_loocv_union_recons.tif")
# recon.rast <- calc(recon.rast,function(x){x[x<0] <- 0; return(x)})
# writeRaster(recon.rast,"../OUTPUT_FINAL/ZuniCibola_PRISM_grow_prcp_ols_loocv_union_recons.tif", datatype="FLT4S", options=c("COMPRESS=DEFLATE", "ZLEVEL=9", "INTERLEAVE=BAND"), overwrite=T, setStatistics=FALSE)
# 
# ZuniCibola.PRISM.grow.prcp.models.union <- readRDS("../OUTPUT_FINAL/ZuniCibola_PRISM_grow_prcp_ols_loocv_selected_lms_union.rds")
# system.time(ZuniCibola.PRISM.grow.prcp.errors.union <- recon.errors(name="ZuniCibola_PRISM_grow_prcp_ols_loocv_union", models=ZuniCibola.PRISM.grow.prcp.models.union, Ytrain.brick=ZuniCibola.PRISM.grow.prcp, select.stat="CV", recon.series=recon.series, force.redo=T))
