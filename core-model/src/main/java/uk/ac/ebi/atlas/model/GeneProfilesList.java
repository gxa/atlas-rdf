package uk.ac.ebi.atlas.model;

import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Queue;

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
public class GeneProfilesList<T extends Profile> extends ArrayList<T> {

    private static final long serialVersionUID = -1678371004778942235L;

    private Integer totalResultCount = 0;

    public GeneProfilesList(Collection<T> collection) {
        super(collection);
    }

    public GeneProfilesList() {
    }

    @Override
    public List<T> subList(int fromIndex, int toIndex) {
        checkArgument(0 <= toIndex, "Upper index value must be larger than 0");
        if (toIndex > size()) {
            return this;
        }
        return super.subList(fromIndex, toIndex);
    }

    public Integer getTotalResultCount() {
        return totalResultCount;
    }

    public void setTotalResultCount(int totalResultCount) {
        this.totalResultCount = totalResultCount;
    }

    public ImmutableList<String> extractGeneNames() {
        ImmutableList.Builder<String> builder = ImmutableList.builder();
        for (T profile : this) {
            builder.add(profile.getName());
        }
        return builder.build();
    }

    // add all from queue, in order they come off the queue
    public void addAll(Queue<T> queue) {
        T profile;
        while ((profile = queue.poll()) != null) {
            this.add(profile);
        }
    }

}