package com.qianfeng.interview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 从题库中随机抽取五道题目
 */
public class QuestionActivity extends AppCompatActivity
{
    private ListView listView;

    private List<Question> list = new ArrayList<>();

    private QuestionAdatper adatper;

    private final int count = 10;

    private final boolean isInterView = true;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        listView = (ListView) findViewById(R.id.question_lv);

        adatper = new QuestionAdatper(this, list);

        listView.setAdapter(adatper);

        InputStream inputStream = null;
        try
        {
            inputStream = getResources().getAssets().open("question.xml");

            List<Question> questions = null;

            if (isInterView)
            {
                questions = getRandomList(parse(inputStream));
            }
            else
            {
                questions = parse(inputStream);
            }
            list.addAll(questions);
            adatper.notifyDataSetChanged();

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (inputStream != null)
            {
                try
                {
                    inputStream.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }

    }

    private List<Question> getRandomList(List<Question> list)
    {
        List<Question> targetList = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < count; i++)
        {
            int r = random.nextInt(list.size());
            Question question = list.remove(r);
            targetList.add(question);
        }

        return targetList;
    }

    private List<Question> parse(InputStream inputStream)
    {
        if (inputStream == null)
        {
            return null;
        }

        List<Question> list = new ArrayList<>();

        XmlPullParserFactory factory = null;

        try
        {
            factory = XmlPullParserFactory.newInstance();

            factory.setNamespaceAware(true);

            XmlPullParser xpp = factory.newPullParser();

            xpp.setInput(inputStream, "utf-8");

            int type = xpp.getEventType();

            Question question = null;

            while (type != XmlPullParser.END_DOCUMENT)
            {
                switch (type)
                {
                    case XmlPullParser.START_DOCUMENT:
                        String name = xpp.getName();
                        Log.d("tag", "name = " + name);
                        break;

                    case XmlPullParser.START_TAG:
                        String startTag = xpp.getName();

                        if ("item".equals(startTag))
                        {
                            question = new Question();
                        }
                        else if ("question".equals(startTag))
                        {
                            String qu = xpp.nextText();
                            Log.d("tag", "qu = " + qu);
                            question.setTitle(qu);
                        }
                        else if ("answer".equals(startTag))
                        {
                            String an = xpp.nextText();
                            Log.d("tag", "an = " + an);
                            question.setAnswer(an);
                        }

                        break;

                    case XmlPullParser.END_TAG:
                        String endTag = xpp.getName();

                        if ("item".equals(endTag))
                        {
                            list.add(question);
                        }
                        Log.d("tag", "endTag = " + endTag);
                        break;
                }

                type = xpp.next();
            }
        }
        catch (XmlPullParserException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return list;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        list.clear();
        list = null;
    }
}
