package org.jorm.annotations;

import org.jorm.RelationshipType;

public @interface Relationship {
    RelationshipType type() default RelationshipType.One_To_One;
    String[] joinColumn() default {} ;
}
