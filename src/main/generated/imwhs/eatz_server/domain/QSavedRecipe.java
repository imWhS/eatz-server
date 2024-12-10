package imwhs.eatz_server.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QSavedRecipe is a Querydsl query type for SavedRecipe
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QSavedRecipe extends EntityPathBase<SavedRecipe> {

    private static final long serialVersionUID = -1449120983L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QSavedRecipe savedRecipe = new QSavedRecipe("savedRecipe");

    public final imwhs.eatz_server.common.QBaseEntity _super = new imwhs.eatz_server.common.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> deletedAt = _super.deletedAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QRecipe recipe;

    public final ListPath<SavedRecipeSchedule, QSavedRecipeSchedule> schedules = this.<SavedRecipeSchedule, QSavedRecipeSchedule>createList("schedules", SavedRecipeSchedule.class, QSavedRecipeSchedule.class, PathInits.DIRECT2);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public final QEatzUser user;

    public QSavedRecipe(String variable) {
        this(SavedRecipe.class, forVariable(variable), INITS);
    }

    public QSavedRecipe(Path<? extends SavedRecipe> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QSavedRecipe(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QSavedRecipe(PathMetadata metadata, PathInits inits) {
        this(SavedRecipe.class, metadata, inits);
    }

    public QSavedRecipe(Class<? extends SavedRecipe> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.recipe = inits.isInitialized("recipe") ? new QRecipe(forProperty("recipe"), inits.get("recipe")) : null;
        this.user = inits.isInitialized("user") ? new QEatzUser(forProperty("user")) : null;
    }

}

