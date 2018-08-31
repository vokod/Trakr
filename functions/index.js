const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp();
// admin.initializeApp(functions.config().firebase);

/*exports.deleteUserTracks = functions.auth.user().onDelete(event => {
    const user = event.data; // The Firebase user.
    const email = user.email;
    const uid = user.uid;
    console.log('A user is deleted. Email: ', email, ', uid: ',uid);
    const userTracks = '/' + uid;
    admin.database().ref(userTracks).remove();
});*/

exports.deleteUserTracks = functions.auth.user().onDelete((user) => {
    const email = user.email;
    const uid = user.uid;
    console.log('A user is deleted. Email: ', email, ', uid: ',uid);
    const userTracks = '/' + uid;
    admin.database().ref(userTracks).remove();
    return 0
});
