import org.hortonmachine.modules.*
import org.hortonmachine.*

def baseFolder = "/home/hydrologis/development/hm_models_testdata/invest_swy/"

def month = 4
def monthStr = "${month-1}"

def quickflowInputsFolder = baseFolder + "/01_input_quickflow/"
def quickflowOutputsFolder = baseFolder + "/01_output_quickflow/"
if( !new File(quickflowOutputsFolder).exists() ){
    new File(quickflowOutputsFolder).mkdirs()
}

def rainMap = quickflowInputsFolder + "prcp_a"+monthStr+".tif"
def netMap = quickflowInputsFolder + "hm_denet1000_gura.tif"
def cnMap = quickflowInputsFolder + "CN.tif"
def eventsMap = quickflowInputsFolder + "n_events"+monthStr+".tif"
def quickflowMap = quickflowOutputsFolder + "hm_quickflow"+monthStr+".tif"

def petInputFolder = baseFolder + "02_input_pet/"
def petOutputFolder = baseFolder + "02_output_pet/"
if( !new File(petOutputFolder).exists() ){
    new File(petOutputFolder).mkdirs()
}

def referenceEtpMap = petInputFolder + "et0_a"+monthStr+".tif"
def kcMap = petInputFolder + "kc_"+monthStr+".tif"
def petMap = petOutputFolder + "hm_pet"+monthStr+".tif"

def rechargeInputFolder = baseFolder + "03_input_recharge/"
def rechargeOutputFolder = baseFolder + "03_output_recharge/"
if( !new File(rechargeOutputFolder).exists() ){
    new File(rechargeOutputFolder).mkdirs()
}

def flowMap = rechargeInputFolder + "hm_dedrain_gura.tif"
def rechargeMap = rechargeOutputFolder + "hm_recharge"+monthStr+".tif"
def availableRechargeMap = rechargeOutputFolder + "hm_available_recharge"+monthStr+".tif"
def aetMap = rechargeOutputFolder + "hm_aet"+monthStr+".tif"
def upslopeSubsidyMap = rechargeOutputFolder + "hm_upslopeSubsidy"+monthStr+".tif"
def alpha = 1.0/12.0

def baseflowInputsFolder = baseFolder + "04_input_baseflow/"
def baseflowOutputFolder = baseFolder + "04_output_baseflow/"
if( !new File(baseflowOutputFolder).exists() ){
    new File(baseflowOutputFolder).mkdirs()
}

def bfMap = baseflowOutputFolder + "hm_baseflow"+monthStr+".tif"
def lsumMap = baseflowOutputFolder + "hm_lsum"+monthStr+".tif"
def vriMap = baseflowOutputFolder + "hm_vri"+monthStr+".tif"
def biMap = baseflowOutputFolder + "hm_b"+monthStr+".tif"

//println HM.printColorTables()

def doScs = true
def doPet = true
def doRech = true
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

if(doRech){
    def v = new SWYRechargeRouting()
    v.pAlpha = alpha
    v.inPet = petMap
    v.inFlowdirections = flowMap
    v.inNet = netMap
    v.inPrecipitation = rainMap
    v.inQuickflow = quickflowMap
    v.outAet = aetMap
    v.outRecharge = rechargeMap
    v.outUpslopeSubsidy = upslopeSubsidyMap
    v.outAvailableRecharge = availableRechargeMap
    v.process()
    HM.makeQgisStyleForRaster("blues", rechargeMap, 3)
    HM.makeQgisStyleForRaster("blues", availableRechargeMap, 3)
    HM.makeQgisStyleForRaster("reds", aetMap, 3)
    HM.makeQgisStyleForRaster("violets", upslopeSubsidyMap, 3)
}

if(doBaseFlow){
    def b = new SWYBaseflowRouting()
    b.inRecharge = rechargeMap
    b.inAvailableRecharge = availableRechargeMap
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