package imwhs.eatz_server.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QRating is a Querydsl query type for Rating
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QRating extends EntityPathBase<Rating> {

    private static final long serialVersionUID = -2105400087L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QRating rating = new QRating("rating");

    public final imwhs.eatz_server.common.QBaseEntity _super = new imwhs.eatz_server.common.QBaseEntity(this);

    public final StringPath content = createString("content");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> deletedAt = _super.deletedAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final BooleanPath isHidden = createBoolean("isHidden");

    public final QRecipe recipe;

    public final NumberPath<Integer> score = createNumber("score", Integer.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public final QEatzUser user;

    public QRating(String variable) {
        this(Rating.class, forVariable(variable), INITS);
    }

    public QRating(Path<? extends Rating> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QRating(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QRating(PathMetadata metadata, PathInits inits) {
        this(Rating.class, metadata, inits);
    }

    public QRating(Class<? extends Rating> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.recipe = inits.isInitialized("recipe") ? new QRecipe(forProperty("recipe"), inits.get("recipe")) : null;
        this.user = inits.isInitialized("user") ? new QEatzUser(forProperty("user")) : null;
    }

}

