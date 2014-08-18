package uk.ac.ebi.spot.rdf.model.baseline;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;

import java.util.*;

/*
 * Copyright 2008-2013 Microarray Informatics Team, EMBL-European Bioinformatics Institute
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 * For further details of the Gene Expression Atlas project, including source code,
 * downloads and documentation, please see:
 *
 * http://gxa.github.com/gxa
 */
public class FactorSet implements FactorGroup {

    private Map<String, Factor> factorsByType = new HashMap<>();

    public FactorSet() {}

    FactorSet(Map<String, Factor> factorsByType) {
        this.factorsByType = factorsByType;
    }

    public FactorSet(Collection<Factor> factors) {
        addAll(factors);
    }

    public FactorSet add(Factor factor) {
        factorsByType.put(factor.getType(), factor);
        return this;
    }

    public FactorSet addAll(Collection<Factor> factors) {
        for (Factor factor : factors){
            add(factor);
        }
        return this;
    }

    @Override
    public Iterator<Factor> iterator() {
        return Iterators.unmodifiableIterator(factorsByType.values().iterator());
    }


    @Override
    public Factor getFactorByType(String type) {
        return factorsByType.get(type);
    }

    @Override
    public boolean containsAll(Set<Factor> factors) {
        return factorsByType.values().containsAll(factors);
    }

    @Override
    public int compareTo(FactorGroup other) {
        if (this.size() != other.size()) {
            return Integer.compare(this.size(), other.size());
        }

        Iterator<Factor> thisIterator = this.iterator();
        Iterator<Factor> otherIterator = other.iterator();

        while (thisIterator.hasNext()) {
            Factor thisFactor = thisIterator.next();
            Factor otherFactor = otherIterator.next();
            int c = thisFactor.compareTo(otherFactor);
            if (c != 0) {
                return c;
            }
        }

        return 0;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        //equality on hashmaps or hashmap values is broken, for this reason better to compare brand new Lists
        List<Factor> thisFactors = Lists.newArrayList(factorsByType.values());
        List<Factor> otherFactors =  Lists.newArrayList(((FactorSet) other).factorsByType.values());

        return com.google.common.base.Objects.equal(thisFactors, otherFactors);
    }

    @Override
    public int hashCode() {
        // hashcode on factorsByType.values() doesn't work
        return com.google.common.base.Objects.hashCode(factorsByType.entrySet());
    }

    @Override
    public String toString() {
        return com.google.common.base.Objects.toStringHelper(this.getClass())
                .add("factors", factorsByType.values())
                .add("hasCode", hashCode())
                .toString();
    }

    @Override
    public boolean overlapsWith(Collection<Factor> factors) {
        return !Collections.disjoint(factorsByType.values(), factors);
    }

    @Override
    public List<Factor> remove(Collection<Factor> factors) {
        ArrayList<Factor> allFactors = Lists.newArrayList(factorsByType.values());

        allFactors.removeAll(factors);

        return allFactors;
    }

    @Override
    public FactorSet removeType(String factorType) {
        HashMap<String, Factor> factorsByTypeClone = new HashMap<>(factorsByType);
        factorsByTypeClone.remove(factorType);
        return new FactorSet(factorsByTypeClone);
    }

    @Override
    public boolean contains(Factor factor) {
        return factorsByType.containsValue(factor);
    }

    // Map <type, value>
    public static FactorSet create(Map<String, String> factorValueByType) {
        FactorSet factorSet = new FactorSet();
        for (String factorType : factorValueByType.keySet()) {
            Factor factor = new Factor(factorType, factorValueByType.get(factorType));
            factorSet.add(factor);
        }

        return factorSet;
    }

    public int size() {
        return factorsByType.size();
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

}
