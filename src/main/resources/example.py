
import netCDF4
import numpy as np
from netCDF4 import ma
import matplotlib.pyplot as plt
from matplotlib.backends.backend_pdf import PdfPages
import sys

## @begin main
#  @in LandWaterMask_Global_CRUNCEP.nc
#  @in NEE_first_year.nc
#  @out result_simple.pdf

def main(sys.argv):

    #admin
    if len(argv) < 1:
        fSim='BG1' #default simulation
    else:
        fSim = argv[0]

    db_pth = "./" #customize with your path
    adj = 60*60*24*(365/12)*1000 #unit conversion factor: kgC/m2/s -> gC/m2/mon
    nmons = 12*(2010-1901+1) #time vector start
    isat = np.array(range(1982,2010,1))
    idx = 1  #set counter


    ## @begin fetch_mask
    #  @in "LandWaterMask_Global_CRUNCEP.nc" @as input_mask_file
    #  @out mask @as land_water_mask

    g = netCDF4.Dataset(db_pth+'land_water_mask/LandWaterMask_Global_CRUNCEP.nc', 'r')
    mask=g.variables['land_water_mask']
    mask = mask[:].swapaxes(0,1)

    ## @end fetch_mask
    
    
    #pick models
    #model=['clm'] #one model only for simple use case
    #for fmodel in model:
    fmodel = 'clm'


    ## @begin load_data
    #  @in "CLM4_BG1_V1_Monthly_NEE.nc4" @as input data file
    #  @out data @as NEE_data

    f = netCDF4.Dataset(db_pth+'NEE_first_year.nc', 'r')
    data = f.variables['NEE']
    data = data[:]  # Make a copy
    data = data.swapaxes(0,2)
    #data = data.swapaxes(1,2)
    data = data*adj # adjust data unit

    ## @end load_data


    ## @begin standardize_with_mask
    #  @in data @as NEE_data
    #  @in mask @as land_water_mask
    #  @out data @as standardized_NEE_data

    #standardize land mask
    native = data.mean(2)
    latShape = mask.shape[0]
    logShape = mask.shape[1]
    for x in range(latShape): # BL: fix X-Y confusion
        for y in range(logShape):
            #print(mask[x,y])
            if mask[x,y] == 1 and ma.getmask(native[x,y]) == 1:
            #enumerate time dimension fixing lat and long and set to 0
                for index in range(data.shape[2]):
                    data[x,y,index] = 0

    ## @end standardize_mask


    ## @begin simple_diagnose
    #  @in np @as standardized_NEE_data
    #  @out pp @as result_NEE_pdf

    #gridded array
    plt.imshow(np.mean(data,2))
    plt.xlabel("Mean 1982-2010 NEE [gC/m2/mon]")
    #plt.title(fmodel + ":" + fSim)
    plt.title(fmodel + ":BG1")
    pp = PdfPages('result_NEE.pdf')
    pp.savefig()
    pp.close()
    #plt.show()

    ## @end simple_diagnose

#if __name__ == "__main__":
#    print("running...")

## @end main