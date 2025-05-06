package Service;


import Model.Post;
import Model.User;

import java.util.List;

public interface PostService
{
    void createPost(String username, String content, String imageUrl, String link);
    void DeletePost(long postID,String username);
     void UpdatePost(long postID,Post newPost,String username);
    void likePost(long postID,String username,String friendName);
    void removelikefromPost(long postID,String username,String friendName);
    void commentPost(long postID, String comment,String username,String friendName);
    void removeComment(long postID, long commentID,String username,String friendName);
    void updateComment(long postID, long commentID, String newComment,String username);
    List<Post> Getallposts();
}



