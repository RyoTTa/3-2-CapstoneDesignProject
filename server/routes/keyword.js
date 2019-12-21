
var ObjectId =require('mongodb').ObjectId;

module.exports = function(router,app) {
    console.log('keyword router 선언');

    router.route('/keyword_insert').post(function(req,res){
        console.log('keyword 등록 요청');
        var database = app.get('database');
        var keyword = req.body.keyword;
        var email = req.body.email;
       
        var add_keyword = new database.KeywordModel({
            'keyword' : keyword,
            'email' : email
        });
        add_keyword.save(function(err){
                if(err){
                    throw err;
                }
            });
        console.log('keyword 등록');
        res.send('true');
        });
    
    router.route('/keyword_delete').post(function(req,res){
        console.log('keyword 삭제 요청');

        var database = app.get('database');
        var keyword = req.body.keyword;
        var email = req.body.email;

        database.KeywordModel.remove({'keyword':keyword,'email':email},function(err,doc){
            if(err){
                throw err;
            }
        });
        res.send('true');
    });
};
