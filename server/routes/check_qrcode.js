
module.exports = function(router,app) {
    console.log('check_qrcode 호출됨.');

    router.route('/check_qrcode').get(function(req,res){
        console.log('/check_qrcode요청');
        var email = req.query.email;
        var item_id = req.query._id;
        console.log(email,item_id);
        var database = app.get('database');
        database.ReservationModel.findOne({ 'item_id' : item_id }, function(err, reservation) {
            console.log(reservation);
            if(reservation.borrower_email == email)
                res.send('true');
            else
                res.send('false');
        });
    });
};

//ec2-15-164-51-129.ap-northeast-2.compute.amazonaws.com