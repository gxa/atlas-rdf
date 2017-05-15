package uk.ac.ebi.spot.atlas.rdf;

/**
 * @author Simon Jupp
 * @date 11/08/2014
 * Samples, Phenotypes and Ontologies Team, EMBL-EBI
 */
public enum Organism {

    ANAS_PLATYRHYNCHOS   ("8839", "eukaryote"),
    ANOLIS_CAROLINENSIS  ("28377", "eukaryote"),
    ANOPHELES_GAMBIAE         ("7165", "eukaryote"),
    BOS_TAURUS              ("9913", "eukaryote"),
    CAENORHABDITIS_ELEGANS   ("6239", "eukaryote"),
    CHLOROCEBUS_SABAEUS   ("60711", "eukaryote"),
    CANIS_FAMILIARIS      ("9615", "eukaryote"),
    CIONA_SAVIGNYI       ("51511", "eukaryote"),
    CIONA_INTESTINALIS      ("7719", "eukaryote"),
    DANIO_RERIO             ("7955", "eukaryote"),
    DASYPUS_NOVEMCINCTUS     ("9361", "eukaryote"),
    DROSOPHILA_MELANOGASTER    ("7227", "eukaryote"),
    EQUUS_CABALLUS         ("9796", "eukaryote"),
    GALLUS_GALLUS          ("9031", "eukaryote"),
    GORILLA_GORILLA   ("9593", "eukaryote"),
    HOMO_SAPIENS           ("9606", "eukaryote"),
    MONODELPHIS_DOMESTICA   ("13616", "eukaryote"),
    MUS_MUSCULUS               ("10090", "eukaryote"),
    MACACA_MULATTA         ("9544", "eukaryote"),
    OVIS_ARIES             ("9940", "eukaryote"),
    PAPIO_ANUBIS   ("9555", "eukaryote"),
    RATTUS_NORVEGICUS     ("10116", "eukaryote"),
    SCHISTOSOMA_MANSONI   ("6183", "eukaryote"),
    SUS_SCROFA        ("9823", "eukaryote"),
    TETRAODON_NIGROVIRIDIS  ("99883", "eukaryote"),
    XENOPUS_TROPICALIS      ("8364", "eukaryote"),
    XENOPUS_SILURANA_TROPICALIS ("8364", "eukaryote"),


    ARABIDOPSIS_LYRATA   ("59689", "plants"),
    ARABIDOPSIS_THALIANA    ("3702", "plant"),
    BRACHYPODIUM_DISTACHYON   ("15368", "plant"),
    BRASSICA_OLERACEA ("3712", "plant"),
    BRASSICA_RAPA_SUBSP_RAPA   ("51350", "plant"),
    GLYCINE_MAX   ("3847", "plant"),
    HORDEUM_VULGARE   ("4513", "plant"),
    HORDEUM_VULGARE_SUBSP_VULGARE   ("112509", "plant"),
    MEDICAGO_TRUNCATULA   ("3880", "plant"),
    ORYZA_SATIVA              ("4530", "plant"),
    ORYZA_SATIVA_INDICA_GROUP   ("39946", "plant"),
    ORYZA_SATIVA_JAPONICA  ("39947", "plant"),
    ORYZA_SATIVA_JAPONICA_GROUP  ("39947", "plant"),
    PHYSCOMITRELLA_PATENS   ("3218", "plant"),
    POPULUS_TRICHOCARPA     ("3694", "plant"),
    SOLANUM_LYCOPERSICUM   ("4081", "plant"),
    SOLANUM_TUBEROSUM   ("4113", "plans"),
    SORGHUM_BICOLOR   ("4558", "plant"),
    THEOBROMA_CACAO   ("4113", "plant"),
    TRITICUM_AESTIVUM   ("4565", "plant"),
    VITIS_VINIFERA       ("29760", "plant"),
    ZEA_MAYS   ("4577", "plant"),

    ASPERGILLUS_FUMIGATUS ("746128", "fungi"),
    ASPERGILLUS_FUMIGATUS_AF293 ("330879", "fungi"),
    SCHIZOSACCHAROMYCES_POMBE    ("4896", "fungi"),
    SACCHAROMYCES_CEREVISIAE ("4932", "fungi");

    String id;
    String kingdom;


    Organism(String id, String kingdom) {
        this.id = id;
        this.kingdom = kingdom;
    }

    public String getId () {
        return id;
    }
    public String getKingdom() {
        return kingdom;
    }

    public static boolean isValid(String speciesName) {

        for(Organism v : Organism.values()) {
            if (v.name().equals(clean(speciesName))) {
                return true;
            }
        }
        return false;
    }


    public static Organism getOrganimsByName(String speciesName) {
        for(Organism v : Organism.values()) {
            if (v.name().equals(clean(speciesName))) {
                return v;
            }
        }
        return null;
    }

    public static Organism getOrganimsById(String organsimId) {
        for (Organism o : Organism.values()) {
            if (organsimId.equals(o.getId())) {
                return o;
            }
        }
        return null;
    }

    public static String clean (String id) {
        return id.toUpperCase().replaceAll(" ", "_").replace("(", "").replace(")","").replace(".", "");

    }
}
