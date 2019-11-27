var FCM = require('fcm-node');
var serverKey = 'AAAAwELGJPA:APA91bFGTDAuM8Ycc-bsZD2XpfW6uwmxQBqMkgp0ziESv9KcotpFpQOTYVQ2RyMSm5CR6SfZh8Rrtc-_hmEOzR9YOM2M5WqSitcxb4E9LsbdX9RH9ialuFkNUHbUkheS7lUOcFkCH4G_';
var fcm = new FCM(serverKey);

module.exports = function(router,app) {
    console.log('fcm_push router 정의');
    router.route('/fcm_push').post(function(req,res){

        console.log('fcm 전송 요청');
        var database = app.get('database');
        var my_email = req.body.my_email;
        var your_email = req.body.your_email;
        var contents = req.body.contents;
        var myname;
        var token;
        database.UserModel.findOne({'email':your_email},function(err,user){
            if(err){
                throw err;
            }
            token = user.token;

            database.UserModel.findOne({'email':my_email},function(err,user){
                if(err){
                    throw err;
                }
                myname = user.name;

                var message = { //this may vary according to the message type (single recipient, multicast, topic, et cetera)
                    to: token, 
                    //collapse_key: 'your_collapse_key',
                    
                    notification: {
                        title: myname, 
                        body: contents 
                    },
                    /*
                    data: {  //you can send only notification or only data(or include both)
                        my_key: 'my value',
                        my_another_key: 'my another value'
                    }
                    */
                };
                console.log(message);
                
                //callback style
                fcm.send(message, function(err, response){
                    if (err) {
                        console.log("Something has gone wrong!");
                    } else {
                        console.log("Successfully sent with response: ", response);
                    }
                });
                console.log('fcm send 완료');
                res.send('true');
                });

            });

        });
       
    

       
};







