package imwhs.eatz_server.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QEatzUser is a Querydsl query type for EatzUser
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QEatzUser extends EntityPathBase<EatzUser> {

    private static final long serialVersionUID = -1526615207L;

    public static final QEatzUser eatzUser = new QEatzUser("eatzUser");

    public final imwhs.eatz_server.common.QBaseEntity _super = new imwhs.eatz_server.common.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> deletedAt = _super.deletedAt;

    public final StringPath email = createString("email");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath password = createString("password");

    public final ListPath<Recipe, QRecipe> recipes = this.<Recipe, QRecipe>createList("recipes", Recipe.class, QRecipe.class, PathInits.DIRECT2);

    public final EnumPath<Role> role = createEnum("role", Role.class);

    public final ListPath<SavedRecipe, QSavedRecipe> savedRecipes = this.<SavedRecipe, QSavedRecipe>createList("savedRecipes", SavedRecipe.class, QSavedRecipe.class, PathInits.DIRECT2);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public final StringPath username = createString("username");

    public QEatzUser(String variable) {
        super(EatzUser.class, forVariable(variable));
    }

    public QEatzUser(Path<? extends EatzUser> path) {
        super(path.getType(), path.getMetadata());
    }

    public QEatzUser(PathMetadata metadata) {
        super(EatzUser.class, metadata);
    }

}

