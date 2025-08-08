/*
 * User (user_id (PK), name, email)
 */
public class User {
    private final int userId;
    private final String name;
    private final String email;

    public User(int userId, String name, String email){
        this.userId = userId;
        this.name = name;
        this.email = email;
    }

    // getters,setters
    public int getUserId(){
        return userId;
    }

}
