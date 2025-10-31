import org.hortonmachine.modules.*
import org.hortonmachine.*

def baseFolder = "/home/hydrologis/development/hm_models_testdata/invest_swy/"

def quickflowInputsFolder = baseFolder + "/01_input_quickflow/"
def quickflowOutputsFolder = baseFolder + "/01_output_quickflow/"
if( !new File(quickflowOutputsFolder).exists() ){
    new File(quickflowOutputsFolder).mkdirs()
}

def rainMap = quickflowInputsFolder + "prcp_a0.tif"
def netMap = quickflowInputsFolder + "hm_denet1000_gura.tif"
def cnMap = quickflowInputsFolder + "CN.tif"
def eventsMap = quickflowInputsFolder + "n_events0.tif"
def quickflowMap = quickflowOutputsFolder + "hm_quickflow.tif"

def petInputFolder = baseFolder + "02_input_pet/"
def petOutputFolder = baseFolder + "02_output_pet/"
if( !new File(petOutputFolder).exists() ){
    new File(petOutputFolder).mkdirs()
}

def referenceEtpMap = petInputFolder + "et0_a0.tif"
def kcMap = petInputFolder + "kc_0.tif"
def petMap = petOutputFolder + "hm_pet.tif"

def rechargeInputFolder = baseFolder + "03_input_recharge/"
def rechargeOutputFolder = baseFolder + "03_output_recharge/"
if( !new File(rechargeOutputFolder).exists() ){
    new File(rechargeOutputFolder).mkdirs()
}

def flowMap = rechargeInputFolder + "hm_dedrain_gura.tif"
def infMap = rechargeOutputFolder + "hm_infiltration.tif"
def netInfMap = rechargeOutputFolder + "hm_netinfiltration.tif"
def aetMap = rechargeOutputFolder + "hm_aet.tif"
def lsumAvailableMap = rechargeOutputFolder + "hm_lsum_available.tif"
def alpha = 1.0/12.0

def baseflowInputsFolder = baseFolder + "04_input_baseflow/"
def baseflowOutputFolder = baseFolder + "04_output_baseflow/"
if( !new File(baseflowOutputFolder).exists() ){
    new File(baseflowOutputFolder).mkdirs()
}

def bfMap = baseflowOutputFolder + "hm_baseflow.tif"
def lsumMap = baseflowOutputFolder + "hm_lsum.tif"
def vriMap = baseflowOutputFolder + "hm_vri.tif"
def biMap = baseflowOutputFolder + "hm_b.tif"

//println HM.printColorTables()

def doScs = true
def doPet = true
def doInf = true
def doBaseFlow = true


//HM.makeQgisStyleForRaster("flow", flowMap, 3)

if(doScs){
    def swyQuickflow = new SWYQuickflow()
    swyQuickflow.inRainfall = rainMap
    swyQuickflow.inNet = netMap
    swyQuickflow.inCurveNumber = cnMap
    swyQuickflow.inNumberOfEvents = eventsMap
    swyQuickflow.outQuickflow = quickflowMap
    swyQuickflow.process()
    HM.makeQgisStyleForRaster("blues", quickflowMap, 3)
}

if(doPet){
    def pet = new PotentialEvapotranspiredWaterVolume()
    pet.inCropCoefficient = kcMap
    //pet.inMaxTemp = ""
    //pet.inMinTemp = ""
    //pet.inAtmosphericTemp = ""
    //pet.inSolarRadiation = ""
    //pet.inRainfall = ""
    pet.inReferenceEtp = referenceEtpMap
    pet.outputPet = petMap
    pet.process()
    HM.makeQgisStyleForRaster("blues", petMap, 3)
}

if(doInf){
    def v = new SWYRechargeRouting()
    v.pAlpha = alpha
    v.inPet = petMap
    v.inFlowdirections = flowMap
    v.inNet = netMap
    v.inRainfall = rainMap
    v.inRunoff = quickflowMap
    v.outInfiltration = infMap
    v.outNetInfiltration = netInfMap
    v.outAet = aetMap
    v.outLsumAvailable = lsumAvailableMap
    v.process()
    HM.makeQgisStyleForRaster("blues", infMap, 3)
    HM.makeQgisStyleForRaster("blues", netInfMap, 3)
    HM.makeQgisStyleForRaster("reds", aetMap, 3)
    HM.makeQgisStyleForRaster("violets", lsumMap, 3)
}

if(doBaseFlow){
    def b = new BaseflowWaterVolume()
    b.inInfiltration = infMap
    b.inNetInfiltration = netInfMap
    b.inNet = netMap
    b.inFlowdirections = flowMap
    b.outBaseflow = bfMap
    b.outLsum = lsumMap
    b.outB = biMap
    b.outVri = vriMap
    b.process()
    HM.makeQgisStyleForRaster("blues", bfMap, 3)
    HM.makeQgisStyleForRaster("violets", lsumMap, 3)
    HM.makeQgisStyleForRaster("reds", vriMap, 3)
    HM.makeQgisStyleForRaster("greens", biMap, 3)
    
    println "Qb = " + b.outQb
    println "Vri sum = " + b.outVriSum
}