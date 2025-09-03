const functions = require("firebase-functions");
const admin = require("firebase-admin");
admin.initializeApp();

exports.sendNewPostNotification = functions.firestore
  .document("posts/{postId}")
  .onCreate((snap, context) => {
    const newPost = snap.data();

    const payload = {
      notification: {
        title: "새 커뮤니티 글",
        body: `${newPost.title || "새 글이"} 등록되었습니다.`,
        sound: "default"
      },
      topic: "new_post"
    };

    return admin.messaging()
      .send(payload)
      .then((response) => {
        console.log("Successfully sent message:", response);
      })
      .catch((error) => {
        console.error("Error sending message:", error);
      });
  });
