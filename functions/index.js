const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp();
// admin.initializeApp(functions.config().firebase);

exports.deleteUserTracks = functions.auth.user().onDelete((user) => {
    const email = user.email;
    const uid = user.uid;
    console.log('A user is deleted. Email: ', email, ', uid: ',uid);
    const userTracks = '/' + uid;
    return admin.database().ref(userTracks).remove();
});

exports.deleteTrackPoints = functions.database.ref('{uId}/tracks/{tId}')
.onDelete((snapshot, context) => {
    const userId = context.params.uId;
    const trackId = context.params.tId;
    console.log('A track is deleted. UserId: ', userId, ', trackId: ',trackId);
    const trackPoints = '/' + userId + '/trackpoints/' + trackId;
    return admin.database().ref(trackPoints).remove();
});
