const functions = require('firebase-functions');
const admin = require('firebase-admin');
// admin.initializeApp();
admin.initializeApp(functions.config().firebase);

exports.onUserCreate_createUserDoc = functions.auth.user().onCreate((user) => {
  const email = user.email; // The email of the user.
  const displayName = user.displayName; // The display name of the user.
  const userID = user.uid;
  console.log('A user is created. Name: ', displayName, 'Email: ', email, ', uid: ',userID);

  const userObject = {
    name: displayName,
    email: email,
    userId: userID,
    isAdmin: false,
 };
  
  return admin.firestore().collection('users').doc(userID).set(userObject)
});


exports.onUserDelete_deleteUserDoc = functions.auth.user().onDelete((user) => {
  const email = user.email;
  const userID = user.uid;
  console.log('A user is deleted. Email: ', email, ', uid: ',userID);
  return admin.firestore().collection('users').doc(userID).delete();
});


  exports.onTrackDelete_deleteTrackPoints = functions.firestore
    .document('users/{userID}/tracks/{trackID}')
    .onDelete((snap, context) => {
        // Get an object representing the document prior to deletion
      // e.g. {'name': 'Marie', 'age': 66}
      const deletedValue = snap.data();
      const userID = context.params.userID;
      const trackID = context.params.trackID;
      console.log('Deleting trackpoints of user: ', userID,' track: ',trackID);

      const BATCH_SIZE = 500;
      const pointsRef = admin.firestore().collection('users').doc(userID).collection('tracks').doc(trackID).collection('points');

      const deletePoints = deleteCollection(admin.firestore(), pointsRef, BATCH_SIZE)
      return Promise.all([deletePoints]);
   });


   exports.onUserDocDelete_deleteTracks = functions.firestore
   .document('users/{userID}')
   .onDelete((snap, context) => {
       // Get an object representing the document prior to deletion
     // e.g. {'name': 'Marie', 'age': 66}
     const deletedValue = snap.data();
     const userID = context.params.userID;
     console.log('Deleting tracks of user: ', userID);

     const BATCH_SIZE = 500;
     const tracksRef = admin.firestore().collection('users').doc(userID).collection('tracks');

     const deleteTracks = deleteCollection(admin.firestore(), tracksRef, BATCH_SIZE)
     return Promise.all([deleteTracks]);
  });

  
  exports.onUserDocDelete_deleteTrackdatas = functions.firestore
  .document('users/{userID}')
  .onDelete((snap, context) => {
      // Get an object representing the document prior to deletion
    // e.g. {'name': 'Marie', 'age': 66}
    const deletedValue = snap.data();
    const userID = context.params.userID;
    console.log('Deleting tracks of user: ', userID);

    const BATCH_SIZE = 500;
    const tracksRef = admin.firestore().collection('users').doc(userID).collection('trackdatas');

    const deleteTracks = deleteCollection(admin.firestore(), tracksRef, BATCH_SIZE)
    return Promise.all([deleteTracks]);
 });



/**
 * Delete a collection, in batches of batchSize. Note that this does
 * not recursively delete subcollections of documents in the collection
 */
function deleteCollection (db, collectionRef, batchSize) {
    var query = collectionRef.orderBy('__name__').limit(batchSize)

    return new Promise(function (resolve, reject) {
      deleteQueryBatch(db, query, batchSize, resolve, reject)
    })
  }

  function deleteQueryBatch (db, query, batchSize, resolve, reject) {
    query.get()
.then((snapshot) => {
        // When there are no documents left, we are done
        if (snapshot.size === 0) {
          return 0
        }

      // Delete documents in a batch
      var batch = db.batch()
      snapshot.docs.forEach(function (doc) {
        batch.delete(doc.ref)
      })

      return batch.commit().then(function () {
        return snapshot.size
      })
    }).then(function (numDeleted) {
      if (numDeleted <= batchSize) {
        resolve()
        return
      }
      else {
      // Recurse on the next process tick, to avoid
      // exploding the stack.
      return process.nextTick(function () {
        deleteQueryBatch(db, query, batchSize, resolve, reject)
      })
    }
  })
    .catch(reject)
  }