package uk.ac.ebi.spot.atlas.rdf.dao;

import com.google.common.collect.ImmutableMap;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;

public class OrganismEnsemblDAO {

    private static final String SELECT_ENSEMBLDB =
            "SELECT ORGANISM_ENSEMBLDB.ENSEMBLDB, BIOENTITY_ORGANISM.NAME FROM ORGANISM_ENSEMBLDB" +
            " JOIN BIOENTITY_ORGANISM ON ORGANISM_ENSEMBLDB.ORGANISMID=BIOENTITY_ORGANISM.ORGANISMID";

    private JdbcTemplate jdbcTemplate;

    public OrganismEnsemblDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public ImmutableMap<String, String> getOrganismEnsemblMap() {

        ImmutableMap.Builder<String, String> mapBuilder = new ImmutableMap.Builder<>();

        List<Map<String, Object>> ensemblNames = jdbcTemplate.queryForList(SELECT_ENSEMBLDB);

        for (Map<String, Object> it : ensemblNames) {
            String name = (String) it.get("NAME");
            String ensemblDb = (String)it.get("ENSEMBLDB");
            mapBuilder.put(name.toLowerCase(), ensemblDb);
        }

        return mapBuilder.build();
    }

}