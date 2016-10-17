package uk.ac.ebi.spot.atlas.rdf.profiles;

import uk.ac.ebi.spot.rdf.model.Expression;

import java.util.Queue;

public interface ExpressionsRowDeserializer<V, T extends Expression> {
    // Because @SafeVarArgs can’t be added to non-final methods...
    @SuppressWarnings ({"unchecked", "varargs"})
    ExpressionsRowDeserializer<V, T> reload(V... values);
    T next();
    T nextExpression(Queue<V> expressionLevelsBuffer);
}
