import org.hortonmachine.modules.*
import org.hortonmachine.*

def baseFolder = "/home/hydrologis/development/hm_models_testdata/saintgeo/geoframe/"
def gpkg = baseFolder + "geodata.gpkg"

def saintgeo = new SaintGeo()
saintgeo.inRiverPoints = gpkg + "#riverpoints"
saintgeo.inSections = gpkg + "#sections"
saintgeo.inSectionPoints = gpkg + "#sectionpoints"
saintgeo.inDischarge = ""
saintgeo.inDownstreamLevel = ""
saintgeo.inLateralId2Discharge = ""
saintgeo.inConfluenceId2Discharge = ""
saintgeo.pDeltaTMillis = ?
saintgeo.outputLevelFile = ""
saintgeo.outputDischargeFile = ""
saintgeo.process()