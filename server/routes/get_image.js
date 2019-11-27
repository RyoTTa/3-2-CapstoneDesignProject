
module.exports = function(router,fs) {
    console.log('getimage 호출됨.');
    var fs2 = require('fs');

    router.route('/get_image').post(function(req,res){
        console.log('/get_image 요청');
        var file_name = req.body.image_path;
        console.log(file_name);
        console.log(typeof(file_name));
        var path = "./images/";
        path=path.concat(file_name);
        console.log(path);
        fs.readFile(path,function(err,data){
            if(err)
            {
                fs2.readFile('./images/error.png',function(err,data){
                    if(err)
                        throw err;
                    res.write(data);
                    res.end();
                });
            }else{
            res.write(data);
            res.end();
            }
        });
    });

};
