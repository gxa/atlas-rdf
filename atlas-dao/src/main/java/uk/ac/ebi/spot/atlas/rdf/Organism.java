package uk.ac.ebi.spot.atlas.rdf;

/**
 * @author Simon Jupp
 * @date 11/08/2014
 * Samples, Phenotypes and Ontologies Team, EMBL-EBI
 */
public enum Organism {

    ANOLIS_CAROLINENSIS  ("28377", "eukaryote"),
    ANOPHELES_GAMBIAE         ("7165", "eukaryote"),
    BOS_TAURUS              ("9913", "eukaryote"),
    CAENORHABDITIS_ELEGANS   ("6239", "eukaryote"),
    CANIS_FAMILIARIS      ("9615", "eukaryote"),
    CIONA_SAVIGNYI       ("51511", "eukaryote"),
    CIONA_INTESTINALIS      ("7719", "eukaryote"),
    DANIO_RERIO             ("7955", "eukaryote"),
    DASYPUS_NOVEMCINCTUS     ("9361", "eukaryote"),
    DROSOPHILA_MELANOGASTER    ("7227", "eukaryote"),
    EQUUS_CABALLUS         ("9796", "eukaryote"),
    GALLUS_GALLUS          ("9031", "eukaryote"),
    HOMO_SAPIENS           ("9606", "eukaryote"),
    MONODELPHIS_DOMESTICA   ("13616", "eukaryote"),
    MUS_MUSCULUS               ("10090", "eukaryote"),
    MACACA_MULATTA         ("9544", "eukaryote"),
    OVIS_ARIES             ("9940", "eukaryote"),
    RATTUS_NORVEGICUS     ("10116", "eukaryote"),
    SUS_SCROFA        ("9823", "eukaryote"),
    TETRAODON_NIGROVIRIDIS  ("99883", "eukaryote"),
    XENOPUS_TROPICALIS      ("8364", "eukaryote"),
    XENOPUS_SILURANA_TROPICALIS ("8364", "eukaryote"),

    ARABIDOPSIS_THALIANA    ("3702", "plant"),
    BRASSICA_OLERACEA ("3712", "plant"),
    ORYZA_SATIVA              ("4530", "plant"),
    ORYZA_SATIVA_JAPONICA  ("39947", "plant"),
    ORYZA_SATIVA_JAPONICA_GROUP  ("39947", "plant"),
    POPULUS_TRICHOCARPA     ("3694", "plant"),
    VITIS_VINIFERA       ("29760", "plant"),

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
        return id.toUpperCase().replaceAll(" ", "_").replace("(", "").replace(")","");

    }
}
