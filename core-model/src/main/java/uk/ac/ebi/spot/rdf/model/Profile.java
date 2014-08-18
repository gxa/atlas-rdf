package uk.ac.ebi.spot.rdf.model;

import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;

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
public abstract class Profile<K, T extends Expression> {
    protected Map<K, T> expressionsByCondition = new HashMap<K,T>();

    private String id;

    private String name;

    protected Profile(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public boolean isEmpty() {
        return expressionsByCondition.isEmpty();
    }

    public int getSpecificity() {
        return expressionsByCondition.size();
    }

    public Double getKnownExpressionLevel(K condition) {
        Expression expression = expressionsByCondition.get(condition);
        if (expression != null && expression.isKnown()) {
            return expression.getLevel();
        }
        return null;
    }

    public boolean isKnownLevel(K condition){
        Expression expression = expressionsByCondition.get(condition);
        return expression != null && expression.isKnown();
    }

    protected abstract void addExpression(T expression);

    public boolean isExpressedOnAnyOf(Set<K> conditions) {
        checkArgument(CollectionUtils.isNotEmpty(conditions));
        return Sets.intersection(expressionsByCondition.keySet(), conditions).size() > 0;
    }

    public Set<K> getConditions() {
        return Sets.newHashSet(expressionsByCondition.keySet());
    }

    protected Profile addExpression(K condition, T expression) {
        addExpression(expression);
        expressionsByCondition.put(condition, expression);
        return this;
    }

    public T getExpression(K condition) {
        return expressionsByCondition.get(condition);
    }

    public String getName() {
        if (StringUtils.isBlank(name)) {
            return id;
        }
        return name;
    }
}
