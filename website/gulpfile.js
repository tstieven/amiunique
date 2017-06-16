
//var array = Array.from(config);

const changed = require('gulp-changed');
var gulp = require('gulp'),
    merge = require('gulp-merge-json'),
    gutil = require('gulp-util'),
    concat = require('gulp-concat-util'),
    sourcemaps = require('gulp-sourcemaps'),
    addsrc = require('gulp-add-src'),
    gulpif = require('gulp-if'),
    uglify = require('gulp-uglify');

gulp.task('json', function () {
    gulp.src('conf/json/*.json')
        .pipe(sourcemaps.init())
        .pipe(concat('test.json', {newLine: ',\n'}))
        .pipe(concat.header('['))
        .pipe(concat.footer(']'))
        .pipe(sourcemaps.write())
        .pipe(gulp.dest('conf/'))


});

var cpt = 0;
var name;
gulp.task('un', function () {
    var config = require('./conf/test.json');

    var promiseArray = Array.from(config);
    console.log(promiseArray)
    for (var prop in config) {
        if (config[prop].enable === "True") {

            name = config[prop].files + '.js';
            gulp.src('static/js/js/' + config[prop].files + '.js').pipe(gulp.dest('static/js/test'));
            console.log(name);
            console.log(prop);
            console.log('static/js/js/' + config[prop].files + '.js');
        }
    }

});

gulp.task('buildJs', function () {
    gulp.src('static/js/test/*.js')
        .pipe(sourcemaps.init())
        .pipe(concat('test.js', {newLine: '\n \n \n \n'}))
        .pipe(concat.header('var ERROR = "error"\n' +
            'var jsonVal={};\n' +
            'var canvasObj ;\n' +
            '    function generateFingerprint(){\n' +
            '        var fp = {}\n' +
            '        return new Promise(function(resolve, reject) {\n' +
            '            var promiseArray = [];\n'
        ))
        .pipe(concat.footer('   return Promise.all(promiseArray).then(function () {\n' +
            '    return resolve(fp);\n' +
            '});\n' +
            '})\n' +
            '; }\n' +
            '            function map(obj, iterator, context) {\n' +
            '    var results = [];\n' +
            '    if (obj == null) {\n' +
            '        return results;\n' +
            '    }\n' +
            '    if (this.nativeMap && obj.map === this.nativeMap) {\n' +
            '        return obj.map(iterator, context);\n' +
            '    }\n' +
            '    this.each(obj, function (value, index, list) {\n' +
            '        results[results.length] = iterator.call(context, value, index, list);\n' +
            '    });\n' +
            '    return results;\n' +
            '}\n' +
            'function each(obj, iterator, context) {\n' +
            '    if (obj === null) {\n' +
            '        return;\n' +
            '    }\n' +
            '    if (this.nativeForEach && obj.forEach === this.nativeForEach) {\n' +
            '        obj.forEach(iterator, context);\n' +
            '    } else if (obj.length === +obj.length) {\n' +
            '        for (var i = 0, l = obj.length; i < l; i++) {\n' +
            '            if (iterator.call(context, obj[i], i, obj) === {}) {\n' +
            '                return;\n' +
            '            }\n' +
            '        }\n' +
            '    } else {\n' +
            '        for (var key in obj) {\n' +
            '            if (obj.hasOwnProperty(key)) {\n' +
            '                if (iterator.call(context, obj[key], key, obj) === {}) {\n' +
            '                    return;\n' +
            '                }\n' +
            '            }\n' +
            '        }\n' +
            '    }\n' +
            '}\n' +
            'generateFingerprint().then(function (val) {\n' +
            ' jsonVal = val;\n' +
            '    console.log(val);});\n'
        ))
       .pipe(uglify())
        /*.on('error', function (err) {
            gutil.log(gutil.colors.red('[Error]'), err.toString());
        }



        //only uglify if gulp is ran with '--type production'

        .pipe(gutil.env.type === 'production' ? uglify() : gutil.noop())*/
        .pipe(sourcemaps.write())
        .pipe(gulp.dest('public/javascripts/'));
});


//gulp.task('default', ['buildJs']);