
module.exports = function(router,app) {
    console.log('authentication_email 호출됨.');

    router.route('/authentication_email').get(function(req,res){
        console.log('/authentication_email요청');
        var email = req.query.email;
        var database = app.get('database');
        
        database.UserModel.findOne({ 'email' :  email }, function(err, user) {
            user.authentication = 'true';
            user.save(function(err){
                if(err){
                    throw err;
                }
                console.log('user authentication 갱신');
            });
        });
        res.send('인증완료');
    });
};
