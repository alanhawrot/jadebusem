# -*- coding: utf-8 -*-
from south.utils import datetime_utils as datetime
from south.db import db
from south.v2 import SchemaMigration
from django.db import models


class Migration(SchemaMigration):

    def forwards(self, orm):
        # Adding model 'LastTopicCheck'
        db.create_table(u'topics_lasttopiccheck', (
            (u'id', self.gf('django.db.models.fields.AutoField')(primary_key=True)),
            ('topic', self.gf('django.db.models.fields.related.ForeignKey')(related_name='last_check', to=orm['topics.Topic'])),
            ('user', self.gf('django.db.models.fields.related.ForeignKey')(related_name='last_check', to=orm['Users.JadeBusemUser'])),
            ('last_check', self.gf('django.db.models.fields.DateTimeField')()),
        ))
        db.send_create_signal(u'topics', ['LastTopicCheck'])

        # Adding model 'Contributor'
        db.create_table(u'topics_contributor', (
            (u'id', self.gf('django.db.models.fields.AutoField')(primary_key=True)),
            ('topic', self.gf('django.db.models.fields.related.ForeignKey')(to=orm['topics.Topic'])),
            ('contributor', self.gf('django.db.models.fields.related.ForeignKey')(to=orm['Users.JadeBusemUser'])),
        ))
        db.send_create_signal(u'topics', ['Contributor'])

        # Adding field 'Message.author'
        db.add_column(u'topics_message', 'author',
                      self.gf('django.db.models.fields.related.ForeignKey')(default=2, related_name='author', to=orm['Users.JadeBusemUser']),
                      keep_default=False)

        # Deleting field 'Topic.author'
        db.delete_column(u'topics_topic', 'author_id')

        # Deleting field 'Topic.last_check'
        db.delete_column(u'topics_topic', 'last_check')


    def backwards(self, orm):
        # Deleting model 'LastTopicCheck'
        db.delete_table(u'topics_lasttopiccheck')

        # Deleting model 'Contributor'
        db.delete_table(u'topics_contributor')

        # Deleting field 'Message.author'
        db.delete_column(u'topics_message', 'author_id')

        # Adding field 'Topic.author'
        db.add_column(u'topics_topic', 'author',
                      self.gf('django.db.models.fields.related.ForeignKey')(default=2, related_name='topics', to=orm['Users.JadeBusemUser']),
                      keep_default=False)

        # Adding field 'Topic.last_check'
        db.add_column(u'topics_topic', 'last_check',
                      self.gf('django.db.models.fields.DateTimeField')(default=1),
                      keep_default=False)


    models = {
        u'Users.jadebusemuser': {
            'Meta': {'object_name': 'JadeBusemUser'},
            'address': ('django.db.models.fields.CharField', [], {'max_length': '200', 'blank': 'True'}),
            'company_name': ('django.db.models.fields.CharField', [], {'max_length': '200', 'blank': 'True'}),
            'date_joined': ('django.db.models.fields.DateTimeField', [], {'default': 'datetime.datetime.now'}),
            'email': ('django.db.models.fields.EmailField', [], {'unique': 'True', 'max_length': '254'}),
            'first_name': ('django.db.models.fields.CharField', [], {'max_length': '30', 'blank': 'True'}),
            'groups': ('django.db.models.fields.related.ManyToManyField', [], {'to': u"orm['auth.Group']", 'symmetrical': 'False', 'blank': 'True'}),
            u'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'is_active': ('django.db.models.fields.BooleanField', [], {'default': 'True'}),
            'is_staff': ('django.db.models.fields.BooleanField', [], {'default': 'False'}),
            'is_superuser': ('django.db.models.fields.BooleanField', [], {'default': 'False'}),
            'last_login': ('django.db.models.fields.DateTimeField', [], {'default': 'datetime.datetime.now'}),
            'last_name': ('django.db.models.fields.CharField', [], {'max_length': '30', 'blank': 'True'}),
            'password': ('django.db.models.fields.CharField', [], {'max_length': '128'}),
            'user_id': ('django.db.models.fields.CharField', [], {'max_length': '200', 'blank': 'True'}),
            'user_permissions': ('django.db.models.fields.related.ManyToManyField', [], {'to': u"orm['auth.Permission']", 'symmetrical': 'False', 'blank': 'True'})
        },
        u'auth.group': {
            'Meta': {'object_name': 'Group'},
            u'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'name': ('django.db.models.fields.CharField', [], {'unique': 'True', 'max_length': '80'}),
            'permissions': ('django.db.models.fields.related.ManyToManyField', [], {'to': u"orm['auth.Permission']", 'symmetrical': 'False', 'blank': 'True'})
        },
        u'auth.permission': {
            'Meta': {'ordering': "(u'content_type__app_label', u'content_type__model', u'codename')", 'unique_together': "((u'content_type', u'codename'),)", 'object_name': 'Permission'},
            'codename': ('django.db.models.fields.CharField', [], {'max_length': '100'}),
            'content_type': ('django.db.models.fields.related.ForeignKey', [], {'to': u"orm['contenttypes.ContentType']"}),
            u'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'name': ('django.db.models.fields.CharField', [], {'max_length': '50'})
        },
        u'contenttypes.contenttype': {
            'Meta': {'ordering': "('name',)", 'unique_together': "(('app_label', 'model'),)", 'object_name': 'ContentType', 'db_table': "'django_content_type'"},
            'app_label': ('django.db.models.fields.CharField', [], {'max_length': '100'}),
            u'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'model': ('django.db.models.fields.CharField', [], {'max_length': '100'}),
            'name': ('django.db.models.fields.CharField', [], {'max_length': '100'})
        },
        u'schedules.schedule': {
            'Meta': {'object_name': 'Schedule'},
            'author': ('django.db.models.fields.related.ForeignKey', [], {'to': u"orm['Users.JadeBusemUser']"}),
            'company': ('django.db.models.fields.CharField', [], {'max_length': '200'}),
            u'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'image_path': ('django.db.models.fields.files.FileField', [], {'max_length': '100', 'null': 'True', 'blank': 'True'}),
            'verified': ('django.db.models.fields.BooleanField', [], {'default': 'False'})
        },
        u'topics.contributor': {
            'Meta': {'object_name': 'Contributor'},
            'contributor': ('django.db.models.fields.related.ForeignKey', [], {'to': u"orm['Users.JadeBusemUser']"}),
            u'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'topic': ('django.db.models.fields.related.ForeignKey', [], {'to': u"orm['topics.Topic']"})
        },
        u'topics.lasttopiccheck': {
            'Meta': {'object_name': 'LastTopicCheck'},
            u'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'last_check': ('django.db.models.fields.DateTimeField', [], {}),
            'topic': ('django.db.models.fields.related.ForeignKey', [], {'related_name': "'last_check'", 'to': u"orm['topics.Topic']"}),
            'user': ('django.db.models.fields.related.ForeignKey', [], {'related_name': "'last_check'", 'to': u"orm['Users.JadeBusemUser']"})
        },
        u'topics.message': {
            'Meta': {'object_name': 'Message'},
            'author': ('django.db.models.fields.related.ForeignKey', [], {'related_name': "'author'", 'to': u"orm['Users.JadeBusemUser']"}),
            'date': ('django.db.models.fields.DateTimeField', [], {'auto_now': 'True', 'blank': 'True'}),
            u'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'message': ('django.db.models.fields.TextField', [], {}),
            'topic': ('django.db.models.fields.related.ForeignKey', [], {'related_name': "'messages'", 'to': u"orm['topics.Topic']"})
        },
        u'topics.topic': {
            'Meta': {'object_name': 'Topic'},
            'closed': ('django.db.models.fields.BooleanField', [], {'default': 'False'}),
            u'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'schedule': ('django.db.models.fields.related.ForeignKey', [], {'related_name': "'topics'", 'to': u"orm['schedules.Schedule']"}),
            'topic': ('django.db.models.fields.CharField', [], {'max_length': '200'})
        }
    }

    complete_apps = ['topics']