package one.coffee.sql.entities;

import one.coffee.sql.DB;
import one.coffee.sql.Utils;
import one.coffee.sql.tables.UserConnectionsTable;
import one.coffee.sql.tables.UsersTable;

@CommitOnCreate
public class User
        implements Entity {

    @Argument
    private final long id;
    @Argument
    private final long userId;
    @Argument
    private String city;
    @Argument
    private long stateId;
    @Argument
    private long connectionId;
    private final boolean isCreated;

    public User(long userId, String city, long stateId, long connectionId) {
        this(-1, userId, city, stateId, connectionId);
    }

    public User(long id, long userId, String city, long stateId, long connectionId) {
        if (userId <= 0) {
            throw new IllegalArgumentException("Invalid User id! Got " + userId);
        }

        if (city == null || city.trim().isEmpty()) {
            throw new IllegalArgumentException("User's city can't be empty!");
        }

        this.userId = userId;
        this.city = city;
        this.stateId = stateId;
        this.connectionId = connectionId;

        if (id <= 0) {
            commit();
            this.id = UsersTable.getUserByUserId(userId).getId();
        } else {
            this.id = id;
            if (!DB.hasEntity(UsersTable.INSTANCE, this)) {
                throw new IllegalArgumentException("No User with 'id' = " + id);
            }
        }

        this.isCreated = true;
    }

    public long getId() {
        return id;
    }

    public long getUserId() {
        return userId;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public long getStateId() {
        return stateId;
    }

    public void setStateId(long stateId) {
        this.stateId = stateId;
    }

    public long getConnectionId() {
        return connectionId;
    }

    public void setConnectionId(long connectionId) {
        this.connectionId = connectionId;
    }

    public long getConnectedUserId() {
        if (connectionId <= 0) {
            throw new IllegalStateException(this + " has not connected user!");
        }

        UserConnection userConnection = UserConnectionsTable.getUserConnectionByUserId(userId);
        return userConnection.getUser1Id() == userId ? userConnection.getUser2Id() : userConnection.getUser1Id();
    }

    @Override
    public boolean isCreated() {
        return isCreated;
    }

    @Override
    public String sqlArgValues() {
        StringBuilder sqlValues = new StringBuilder(Utils.SIGNATURE_START);
        if (isCreated()) {
            sqlValues.append(id).append(Utils.ARGS_SEPARATOR);
        }

        sqlValues.append(Utils.SIGNATURE_END);
        return sqlValues.toString();
    }

    @Override
    public void commit() {
        UsersTable.putUser(this);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", userId=" + userId +
                ", city='" + city + '\'' +
                ", stateId=" + stateId +
                ", connectionId=" + connectionId +
                '}';
    }
}
