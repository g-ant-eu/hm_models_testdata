import org.hortonmachine.modules.*
import org.hortonmachine.*

def baseFolder = "/home/hydrologis/storage/lavori_tmp/KLAB/testGura/"

def rainMap = baseFolder + "01_input_runoff/prcp_a0.tif"
def netMap = baseFolder + "01_input_runoff/hm_denet1000_gura.tif"
def cnMap = baseFolder + "01_input_runoff/CN.tif"
def eventsMap = baseFolder + "01_input_runoff/n_events0.tif"
def quickflowMap = baseFolder + "01_output_runoff/hm_runoff.tif"

def referenceEtpMap = baseFolder + "02_input_pet/et0_a0.tif"
def kcMap = baseFolder + "02_input_pet/kc_0.tif"
def petMap = baseFolder + "02_output_pet/hm_pet.tif"

def flowMap = baseFolder + "03_input_recharge/hm_dedrain_gura.tif"
def infMap = baseFolder + "03_output_recharge/hm_infiltration.tif"
def netInfMap = baseFolder + "03_output_recharge/hm_netinfiltration.tif"
def aetMap = baseFolder + "03_output_recharge/hm_aet.tif"
def lsumAvailableMap = baseFolder + "03_output_recharge/hm_lsum_available.tif"
def alpha = 1.0/12.0

def bfMap = baseFolder + "04_output_baseflow/hm_baseflow.tif"
def lsumMap = baseFolder + "04_output_baseflow/hm_lsum.tif"
def vriMap = baseFolder + "04_output_baseflow/hm_vri.tif"
def biMap = baseFolder + "04_output_baseflow/hm_b.tif"

//println HM.printColorTables()

def doScs = false
def doPet = false
def doInf = false
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