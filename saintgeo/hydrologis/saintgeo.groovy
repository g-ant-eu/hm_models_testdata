import org.hortonmachine.modules.*
import org.hortonmachine.*

def baseFolder = "/home/hydrologis/development/hm_models_testdata/saintgeo/hydrologis/"
def gpkg = baseFolder + "geodata.gpkg"
def output = baseFolder + "output/"
if( !new File(output).exists() ){
    new File(output).mkdirs()
}

def saintgeo = new SaintGeo()
saintgeo.inRiverPoints = gpkg + "#riverpoints_adige"
saintgeo.inSections = gpkg + "#sections_adige"
saintgeo.inSectionPoints = gpkg + "#sectionpoints_adige"
saintgeo.inDischarge = baseFolder + "flow/head_discharge.csv"
saintgeo.inDownstreamLevel = baseFolder + "flow/downstream_waterlevel.csv"
saintgeo.inLateralId2Discharge = baseFolder + "flow/q_lateral_offtakes.csv"
saintgeo.pKs = 30.0
saintgeo.pDeltaTMillis = 5000
saintgeo.outTableName = "results"
saintgeo.outGpkg = output + "results.gpkg"
saintgeo.process()