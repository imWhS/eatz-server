package imwhs.eatz_server.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QSavedRecipeSchedule is a Querydsl query type for SavedRecipeSchedule
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QSavedRecipeSchedule extends EntityPathBase<SavedRecipeSchedule> {

    private static final long serialVersionUID = 561768672L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QSavedRecipeSchedule savedRecipeSchedule = new QSavedRecipeSchedule("savedRecipeSchedule");

    public final DatePath<java.time.LocalDate> date = createDate("date", java.time.LocalDate.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Integer> priority = createNumber("priority", Integer.class);

    public final QSavedRecipe savedRecipe;

    public QSavedRecipeSchedule(String variable) {
        this(SavedRecipeSchedule.class, forVariable(variable), INITS);
    }

    public QSavedRecipeSchedule(Path<? extends SavedRecipeSchedule> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QSavedRecipeSchedule(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QSavedRecipeSchedule(PathMetadata metadata, PathInits inits) {
        this(SavedRecipeSchedule.class, metadata, inits);
    }

    public QSavedRecipeSchedule(Class<? extends SavedRecipeSchedule> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.savedRecipe = inits.isInitialized("savedRecipe") ? new QSavedRecipe(forProperty("savedRecipe"), inits.get("savedRecipe")) : null;
    }

}

